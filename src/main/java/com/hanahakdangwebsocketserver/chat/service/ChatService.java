package com.hanahakdangwebsocketserver.chat.service;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.hanahakdangwebsocketserver.chat.dto.ChatResponse;
import com.hanahakdangwebsocketserver.chat.model.Chat;
import com.hanahakdangwebsocketserver.chat.dto.ChatMessage;
import com.hanahakdangwebsocketserver.chat.dto.ChatRequest;
import com.hanahakdangwebsocketserver.chat.mapper.ChatMapper;
import com.hanahakdangwebsocketserver.kafka.KafkaProducer;
import com.hanahakdangwebsocketserver.redis.RedisBoundList;
import static com.hanahakdangwebsocketserver.common.enums.MapperExceptionEnum.CANT_CONVERT_INTO_OBJ;


/**
 * 채팅 로직을 처리하는 서비스
 */
@Log4j2
@Service
public class ChatService {

  @Value("${classroom.chat.key}")
  private String chatChannelName;

  @Value("3")
  private long timeout;

  private final ObjectMapper objectMapper;
  private final KafkaProducer<ChatMessage> kafkaProducer;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final RedisBoundList<Chat> redisBoundList;
  private final TimeUnit timeUnit = TimeUnit.HOURS;

  /**
   * ChatService 생성자
   *
   * @param kafkaTemplate 카프카와 연결한 템플릿
   * @param redisTemplate 레디스와 연결할 템플릿
   * @param objectMapper  json을 object로, object를 json으 변환해주는 맵퍼
   */
  public ChatService(KafkaTemplate<Integer, byte[]> kafkaTemplate,
      RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper,
      SimpMessagingTemplate simpMessagingTemplate) {
    this.objectMapper = objectMapper;
    this.kafkaProducer = new KafkaProducer<>(kafkaTemplate, objectMapper);
    this.redisBoundList = new RedisBoundList<>(redisTemplate, objectMapper);
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  private void publishChat(ChatMessage chatMessage) {
    this.kafkaProducer.publish(chatChannelName, chatMessage);
    log.debug("Published {}", chatMessage);
  }

  private void saveChat(String classroomKey, Chat chatDTO) {
    redisBoundList.setTimeToLive(classroomKey, timeout, timeUnit);
    redisBoundList.push(classroomKey, chatDTO);
    log.debug("Saved {} with ID {}", chatDTO, classroomKey);
  }

  /**
   * 채팅 요청을 처리하는 메서드. 채팅 요청을 발행(publish)하고 저장합니다.
   *
   * @param classroomId 강의실 ID
   * @param chatRequest 채팅 요청
   */
  public void receiveChatRequest(Long classroomId, ChatRequest chatRequest) {
    String classroomKey = classroomId.toString();
    Chat chat = ChatMapper.toModel(chatRequest);
    ChatMessage chatMessage = ChatMapper.toMessage(classroomId, chat);
    publishChat(chatMessage);
    saveChat(classroomKey, ChatMapper.toDTO(chat));
  }

  @KafkaListener(containerFactory = "kafkaListenerContainerFactory", topics = "${classroom.chat.topic-name}")
  public void sendChatResponse(byte[] message) {
    try {
      ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
      log.debug("Received {}", chatMessage);
      ChatResponse chatResponse = ChatMapper.toResponse(chatMessage);
      simpMessagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getClassroomId(),
          chatResponse);
    } catch (IOException e) {
      log.error(CANT_CONVERT_INTO_OBJ.getMessage());
    }
  }
}

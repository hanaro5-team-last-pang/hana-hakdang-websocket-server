package com.hanahakdangwebsocketserver.chat.service;


import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.hanahakdangwebsocketserver.chat.dto.ChatDTO;
import com.hanahakdangwebsocketserver.chat.dto.ChatMessage;
import com.hanahakdangwebsocketserver.chat.dto.ChatRequest;
import com.hanahakdangwebsocketserver.chat.mapper.ChatMapper;
import com.hanahakdangwebsocketserver.chat.model.Chat;
import com.hanahakdangwebsocketserver.redis.RedisBoundList;
import com.hanahakdangwebsocketserver.redis.RedisPubSubService;


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

  private final RedisPubSubService<ChatMessage> redisPubSubService;
  private final RedisBoundList<ChatDTO> redisBoundList;
  private final TimeUnit timeUnit = TimeUnit.HOURS;

  public ChatService(RedisPubSubService<ChatMessage> redisPubSubService,
      RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
    this.redisPubSubService = redisPubSubService;
    this.redisBoundList = new RedisBoundList<>(redisTemplate, objectMapper);
  }


  private void publishChat(ChatMessage chatMessage) {
    Long receivedCount = redisPubSubService.publish(ChannelTopic.of(chatChannelName), chatMessage);
    log.debug("Publish chat. {} received", receivedCount);
  }

  private void saveChat(String classroomKey, ChatDTO chatDTO) {
    redisBoundList.setTimeToLive(classroomKey, timeout, timeUnit);
    redisBoundList.push(classroomKey, chatDTO);
    log.debug("Save {} with ID {}", chatDTO, classroomKey);
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
}

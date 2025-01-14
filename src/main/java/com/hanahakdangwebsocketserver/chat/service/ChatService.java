package com.hanahakdangwebsocketserver.chat.service;


import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.hanahakdangwebsocketserver.chat.dto.ChatMessage;
import com.hanahakdangwebsocketserver.chat.dto.ChatRequest;
import com.hanahakdangwebsocketserver.chat.mapper.ChatMapper;
import com.hanahakdangwebsocketserver.chat.model.Chat;

/**
 * 채팅 로직을 처리하는 서비스
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ChatService {

  private final RedisPubSubService<ChatMessage> redisPubSubService;

  @Value("${classroom.chat.key}")
  private String chatChannelName;

  @Resource(name = "redisTemplate")
  private ZSetOperations<String, Chat> zSetOpts;

  
  private void publishChat(ChatMessage chatMessage) {
    Long receivedCount = redisPubSubService.publish(ChannelTopic.of(chatChannelName), chatMessage);
    log.debug("Publish chat. {} received", receivedCount);
  }

  private void saveChat(String classroomKey, Chat chat, double score) {
    zSetOpts.add(classroomKey, chat, score);
    log.debug("Save {} with score {}", chat, score);
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
    // TODO - 점수 결정 알고리즘 필요
    double score = 12312;
    saveChat(classroomKey, chat, score);
  }
}

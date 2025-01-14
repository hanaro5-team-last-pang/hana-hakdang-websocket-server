package com.hanahakdangwebsocketserver.chat.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.hanahakdangwebsocketserver.chat.dto.ChatMessage;
import com.hanahakdangwebsocketserver.chat.dto.ChatResponse;
import com.hanahakdangwebsocketserver.chat.mapper.ChatMapper;


/**
 * 채팅 메시지 대리자. 레디스 subscribe를 위해 필요
 */
@Log4j2
@RequiredArgsConstructor
public class ChatMessageDelegate {

  private final SimpMessagingTemplate simpMessagingTemplate;

  private final ObjectMapper objectMapper;

  public void handleMessage(String text) {
    try {
      ChatMessage chatMessage = objectMapper.readValue(text, ChatMessage.class);
      ChatResponse chatResponse = ChatMapper.toResponse(chatMessage);
      simpMessagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getClassroomId(),
          chatResponse);
    } catch (JsonProcessingException jsonProcessingException) {
      log.error("Fail to parse json");
    } catch (MessagingException messagingException) {
      log.error("Fail to send message");
    } catch (Exception e) {
      log.error("Unexpected exception occurs", e);
    }
  }
}

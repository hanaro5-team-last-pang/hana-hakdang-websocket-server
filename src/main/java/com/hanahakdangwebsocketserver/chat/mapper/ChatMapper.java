package com.hanahakdangwebsocketserver.chat.mapper;

import com.hanahakdangwebsocketserver.chat.dto.ChatMessage;
import com.hanahakdangwebsocketserver.chat.dto.ChatRequest;
import com.hanahakdangwebsocketserver.chat.dto.ChatResponse;
import com.hanahakdangwebsocketserver.chat.model.Chat;


public class ChatMapper {

  public static Chat toModel(ChatRequest chatRequest) {
    return Chat.builder()
        .username(chatRequest.getUsername())
        .body(chatRequest.getBody())
        .build();
  }

  public static ChatMessage toMessage(Long classroomId, Chat chat) {
    return ChatMessage.builder()
        .classroomId(classroomId)
        .username(chat.getUsername())
        .body(chat.getBody())
        .timestamp(chat.getTimestamp())
        .build();
  }

  public static ChatResponse toResponse(ChatMessage chatMessage) {
    return ChatResponse.builder()
        .username(chatMessage.getUsername())
        .body(chatMessage.getBody())
        .timestamp(chatMessage.getTimestamp())
        .build();
  }
}

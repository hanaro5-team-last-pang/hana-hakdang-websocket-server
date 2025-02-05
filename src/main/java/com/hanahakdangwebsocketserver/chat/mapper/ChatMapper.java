package com.hanahakdangwebsocketserver.chat.mapper;

import com.hanahakdangwebsocketserver.chat.model.Chat;
import com.hanahakdangwebsocketserver.chat.dto.ChatMessage;
import com.hanahakdangwebsocketserver.chat.dto.ChatRequest;
import com.hanahakdangwebsocketserver.chat.dto.ChatResponse;


public class ChatMapper {

  public static Chat toModel(ChatRequest chatRequest) {
    return Chat.builder()
        .userId(chatRequest.getUserId())
        .lectureId(chatRequest.getLectureId())
        .username(chatRequest.getUsername())
        .body(chatRequest.getBody())
        .build();
  }

  public static ChatMessage toMessage(Long classroomId, Chat chat) {
    return ChatMessage.builder()
        .classroomId(classroomId)
        .userId(chat.getUserId())
        .username(chat.getUsername())
        .body(chat.getBody())
        .timestamp(chat.getTimestamp())
        .build();
  }

  public static ChatResponse toResponse(ChatMessage chatMessage) {
    return ChatResponse.builder()
        .userId(chatMessage.getUserId())
        .username(chatMessage.getUsername())
        .body(chatMessage.getBody())
        .timestamp(chatMessage.getTimestamp())
        .build();
  }

  public static Chat toDTO(Chat chat) {
    return Chat.builder()
        .lectureId(chat.getLectureId())
        .userId(chat.getUserId())
        .username(chat.getUsername())
        .body(chat.getBody())
        .timestamp(chat.getTimestamp())
        .build();
  }
}

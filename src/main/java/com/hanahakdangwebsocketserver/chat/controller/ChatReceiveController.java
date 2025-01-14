package com.hanahakdangwebsocketserver.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanahakdangwebsocketserver.chat.dto.ChatRequest;
import com.hanahakdangwebsocketserver.chat.service.ChatService;


@Log4j2
@RestController
@RequiredArgsConstructor
public class ChatReceiveController {

  private final ChatService chatService;

  @MessageMapping("/chat/{classroomId}")
  public void receiveChat(@DestinationVariable("classroomId") Long classroomId,
      @Valid ChatRequest chatRequest) {
    log.debug("Received {} from classroom {}", chatRequest, classroomId);
    chatService.receiveChatRequest(classroomId, chatRequest);
  }
}

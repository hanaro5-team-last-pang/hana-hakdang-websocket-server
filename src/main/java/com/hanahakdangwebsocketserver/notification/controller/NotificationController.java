package com.hanahakdangwebsocketserver.notification.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.hanahakdangwebsocketserver.config.TokenHandler;
import com.hanahakdangwebsocketserver.notification.service.NotificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

  private final TokenHandler tokenHandler;
  private final NotificationService notificationService;

  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public ResponseEntity<SseEmitter> subscribeSSE(
      @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
      HttpServletRequest request
  ) {
    Long userId = tokenHandler.getUserId(request); // JWT에서 userId 추출
    SseEmitter sseEmitter = notificationService.createEmitter(userId);
    return ResponseEntity.ok(sseEmitter);
  }
}

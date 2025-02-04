package com.hanahakdangwebsocketserver.notification.repository;

import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {
  SseEmitter saveEmitter(Long userId, String emitterId, SseEmitter emitter);

  Map<String, SseEmitter> findEmittersByUserId(Long userId);

  void deleteEmitter(Long userId, String emitterId);

  void deleteAllEmittersByUserId(Long userId);
}

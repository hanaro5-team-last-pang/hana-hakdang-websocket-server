package com.hanahakdangwebsocketserver.notification.service;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.hanahakdangwebsocketserver.common.SnowFlakeGenerator;
import com.hanahakdangwebsocketserver.config.TokenHandler;
import com.hanahakdangwebsocketserver.kafka.KafkaNotificationConsumer;
import com.hanahakdangwebsocketserver.notification.dto.NotificationMessage;
import com.hanahakdangwebsocketserver.notification.repository.EmitterRepository;
import static com.hanahakdangwebsocketserver.notification.enums.NotificationResponseExceptionEnum.NOT_VALID_USERID;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

  private final static String KAFKA_TOPIC = "notification"; // topic 이름
  private static final String SSE_NAME = "sse-notification";
  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

  private final SnowFlakeGenerator snowFlakeGenerator;
  private final EmitterRepository emitterRepository;
  private final KafkaNotificationConsumer notificationConsumer;

  public SseEmitter createEmitter(Long userId) throws ResponseStatusException {

    if (userId == null) {
      throw NOT_VALID_USERID.createResponseStatusException();
    }

    String emitterId = String.valueOf(snowFlakeGenerator.nextId());
    SseEmitter emitter = emitterRepository.saveEmitter(userId, emitterId,
        new SseEmitter(DEFAULT_TIMEOUT));

    // SseEmitter가 완료되거나 타임아웃될 때 해당 SseEmitter를 emitterRepository에서 삭제
    emitter.onCompletion(() -> emitterRepository.deleteEmitter(userId, emitterId));
    emitter.onTimeout(() -> emitterRepository.deleteEmitter(userId, emitterId));

    // 첫 접속 시 503 에러를 방지하기 위한 더미 이벤트 전송
    String eventId = "CreatedAt:" + System.currentTimeMillis();
    sendNotification(userId, emitter, eventId, emitterId, "EventStream Created.");

    // TODO : 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방

    return emitter;
  }

  @KafkaListener(topics = KAFKA_TOPIC, groupId = "notified-group")
  public void listen(ConsumerRecord<Integer, byte[]> record) {
    NotificationMessage message = notificationConsumer.consume(record);

    Map<String, SseEmitter> emitters = emitterRepository.findEmittersByUserId(
        message.getReceiverId());
    emitters.forEach((emitterId, emitter) -> {
      log.info("카프카에서 가져온 알림 전송 - emitter id: {}", emitterId);
      sendNotification(message.getReceiverId(), emitter, message.getNotificationId().toString(),
          emitterId, message);
    });
  }

  /**
   * 클라이언트와 연결된 Emitter 객체에 알림 send
   *
   * @param userId    클라이언트의 user id
   * @param emitter   클라이언트와 연결된 Emitter 객체
   * @param eventId   이벤트(알림) id
   * @param emitterId Emitter 객체 id
   * @param data      알림 DTO or 첫 연결 메시지
   */
  private void sendNotification(Long userId, SseEmitter emitter, String eventId, String emitterId,
      Object data) {
    try {
      emitter.send(SseEmitter.event()
          .id(eventId)
          .name(SSE_NAME)
          .data(data)
          .reconnectTime(3000L)  // 클라이언트에서 연결이 끊기고 3초마다 재연결을 시도
      );

      log.info("클라이언트에게 알림 전송 - event id: {}", eventId);

    } catch (IOException exception) {
      emitterRepository.deleteEmitter(userId, emitterId);
    }
  }
}

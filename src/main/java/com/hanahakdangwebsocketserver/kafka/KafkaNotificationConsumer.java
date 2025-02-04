package com.hanahakdangwebsocketserver.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.hanahakdangwebsocketserver.notification.dto.NotificationMessage;
import static com.hanahakdangwebsocketserver.redis.RedisExceptionEnum.CANT_CONVERT_INTO_DTO;

@Log4j2
@RequiredArgsConstructor
@Component
public class KafkaNotificationConsumer {

  private final KafkaTemplate<Integer, byte[]> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public NotificationMessage consume(ConsumerRecord<Integer, byte[]> record) {
    try {
      byte[] messageBytes = record.value();
      return objectMapper.readValue(messageBytes,
          NotificationMessage.class); // byte[] -> JSON -> 객체
    } catch (Exception e) {
      log.error(CANT_CONVERT_INTO_DTO.getMessage());
    }
    return null;
  }
}

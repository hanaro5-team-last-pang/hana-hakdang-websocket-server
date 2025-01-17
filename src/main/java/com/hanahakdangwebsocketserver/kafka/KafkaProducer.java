package com.hanahakdangwebsocketserver.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;

import static com.hanahakdangwebsocketserver.common.enums.MapperExceptionEnum.CANT_CONVERT_INTO_JSON;


@Log4j2
@RequiredArgsConstructor
public class KafkaProducer<V> {

  private final KafkaTemplate<Integer, byte[]> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public void publish(String topic, V message) {
    try {
      log.debug("Sending {} to topic {}", message, topic);
      byte[] mgsByteArr = objectMapper.writeValueAsBytes(message);
      kafkaTemplate.send(topic, mgsByteArr);
    } catch (JsonProcessingException ignored) {
      log.error(CANT_CONVERT_INTO_JSON.getMessage());
    }
  }
}

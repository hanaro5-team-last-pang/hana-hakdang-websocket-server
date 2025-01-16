package com.hanahakdangwebsocketserver.kafka.service;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaProducerService<V> {

  private final KafkaTemplate<String, V> kafkaTemplate;

  public void sendMessage(String topic, V message) {
    kafkaTemplate.send(topic, message);
  }
}

package com.hanahakdangwebsocketserver.config;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServerUrl;

  @Bean
  public Map<String, Object> producerConfigs() {
    // See https://kafka.apache.org/documentation/#producerconfigs for more properties
    return Map.of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUrl,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class
    );
  }

  @Bean
  public ProducerFactory<Integer, byte[]> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }

  @Bean
  public KafkaTemplate<Integer, byte[]> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}

package com.hanahakdangwebsocketserver.config;

import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;


@Configuration
public class KafkaConfig {


  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServerUrl;

  @Value("${classroom.chat.topic-name}")
  private String chatTopicName;

  @Value("${notification.topic-name}")
  private String notificationTopicName;


  @Bean
  public KafkaAdmin admin() {
    Map<String, Object> configs = Map.of(
        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUrl
    );
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic chatTopic() {
    return TopicBuilder.name(chatTopicName)
        .partitions(1)
        .build();
  }

  @Bean
  public NewTopic notificationTopic() {
    return TopicBuilder.name(notificationTopicName)
        .partitions(1)
        .build();
  }
}

package com.hanahakdangwebsocketserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.hanahakdangwebsocketserver.chat.listener.ChatMessageDelegate;


@Configuration
@RequiredArgsConstructor
public class RedisConfig {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate simpMessagingTemplate;

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${classroom.chat.key}")
  private String chatChannelName;

  @Bean
  LettuceConnectionFactory lettuceConnectionFactory() {
    return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
  }

  @Bean
  public RedisTemplate<String, ?> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, ?> redisTemplate = new RedisTemplate<>();

    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(new StringRedisSerializer());

    return redisTemplate;
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListener() {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(lettuceConnectionFactory());
    container.addMessageListener(redisMessageAdapter(), ChannelTopic.of(chatChannelName));
    return container;
  }

  @Bean
  MessageListenerAdapter redisMessageAdapter() {
    return new MessageListenerAdapter(new ChatMessageDelegate(simpMessagingTemplate, objectMapper),
        "handleMessage");
  }

}

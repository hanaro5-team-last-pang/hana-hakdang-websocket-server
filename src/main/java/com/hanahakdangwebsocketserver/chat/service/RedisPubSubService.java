package com.hanahakdangwebsocketserver.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

/**
 * 레디스 pub/sub 서비스
 *
 * @param <V> 레디스의 value 타입을 지정
 */
@Service
@RequiredArgsConstructor
public class RedisPubSubService<V> {

  private final RedisTemplate<String, V> redisTemplate;
  private final RedisMessageListenerContainer redisMessageListenerContainer;

  /**
   * 메시지를 발행하는 메서드
   *
   * @param channelTopic 발행할 채널
   * @param message      발행할 메시지
   * @return 메시지를 수신한 클라이언트의 수를 반환
   */
  public Long publish(ChannelTopic channelTopic, V message) {
    return redisTemplate.convertAndSend(channelTopic.getTopic(), message);
  }

  /**
   * 메시지를 구독하는 메서드.
   *
   * @param channelTopic    구독할 채널
   * @param messageListener 메시지를 소비할 때 실행할 이벤트. 구독은 비동기 처리를 위해
   *                        {@link
   *                        org.springframework.data.redis.listener.RedisMessageListenerContainer}에
   *                        보관됩니다.
   */
  public void subscribe(ChannelTopic channelTopic, MessageListener messageListener) {
    redisMessageListenerContainer.addMessageListener(messageListener, channelTopic);
  }

}



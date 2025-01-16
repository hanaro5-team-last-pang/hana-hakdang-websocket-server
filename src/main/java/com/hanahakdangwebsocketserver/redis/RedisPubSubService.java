package com.hanahakdangwebsocketserver.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import static com.hanahakdangwebsocketserver.redis.MapperExceptionEnum.CANT_CONVERT_INTO_JSON;

/**
 * 레디스 pub/sub 서비스
 *
 * @param <T> 레디스 pub/sub의 메시지 타입을 지정
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class RedisPubSubService<T> {

  private final RedisTemplate<String, String> redisTemplate;
  private final RedisMessageListenerContainer redisMessageListenerContainer;
  private final ObjectMapper objectMapper;

  /**
   * 메시지를 발행하는 메서드
   *
   * @param channelTopic 발행할 채널
   * @param message      발행할 메시지
   * @return 메시지를 수신한 클라이언트의 수를 반환
   */
  public Long publish(ChannelTopic channelTopic, T message) {
    try {
      String msgJsonStr = objectMapper.writeValueAsString(message);
      return redisTemplate.convertAndSend(channelTopic.getTopic(), msgJsonStr);
    } catch (JsonProcessingException e) {
      log.error(CANT_CONVERT_INTO_JSON.getMessage());
      return 0L;
    }
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



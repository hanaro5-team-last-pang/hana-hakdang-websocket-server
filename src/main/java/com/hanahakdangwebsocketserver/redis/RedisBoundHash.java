package com.hanahakdangwebsocketserver.redis;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import static com.hanahakdangwebsocketserver.common.enums.MapperExceptionEnum.CANT_CONVERT_INTO_JSON;
import static com.hanahakdangwebsocketserver.common.enums.MapperExceptionEnum.CANT_CONVERT_INTO_OBJ;

/**
 * 레디스 BoundHash
 *
 * @param <V> 해시에 들어갈 값(value)의 타입
 */
@Log4j2
public class RedisBoundHash<V> {

  private final BoundHashOperations<String, String, String> boundHashOpts;
  private final ObjectMapper objectMapper;

  /**
   * @param key           bound로 설정할 key값.
   * @param redisTemplate 레디스와 연결할 템플릿
   * @param objectMapper  json을 object로, object를 json으로 변환해주는 맵퍼
   */
  public RedisBoundHash(String key,
      RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
    this.boundHashOpts = redisTemplate.boundHashOps(key);
    this.objectMapper = objectMapper;
  }

  /**
   * 해시에 키-값 쌍을 추가합니다.
   *
   * @param key   키
   * @param value 값
   */
  public void put(String key, V value) {
    try {
      String valueJsonStr = objectMapper.writeValueAsString(value);
      log.debug("Putting {}-{} into Redis bound hash", key, value);
      boundHashOpts.put(key, valueJsonStr);
    } catch (JsonProcessingException e) {
      log.error(CANT_CONVERT_INTO_JSON.getMessage());
    }
  }

  /**
   * 해시로부터 키에 해당하는 값을 가져옵니다.
   *
   * @param key   키
   * @param clazz 값의 타입 토큰
   * @return 해시에서 가져온 키
   */
  public Optional<V> get(String key, Class<V> clazz) {
    try {
      String resultStr = boundHashOpts.get(key);
      if (resultStr == null) {
        throw new RuntimeException("값을 찾을 수 없습니다");
      }
      V resultObj = objectMapper.readValue(resultStr, clazz);
      log.debug("Getting {}-{} from Redis bound hash", key, resultObj);
      return Optional.of(resultObj);
    } catch (Exception e) {
      log.error(CANT_CONVERT_INTO_OBJ.getMessage());
      return Optional.empty();
    }
  }

  /**
   * 해시에서 키에 해당하는 원소를 삭제합니다.
   *
   * @param key 키
   * @return 삭제 성공 여부
   */
  public boolean delete(String key) {
    log.debug("Deleting {} from Redis bound hash", key);
    Long result = boundHashOpts.delete(key);
    return result != null;
  }
}

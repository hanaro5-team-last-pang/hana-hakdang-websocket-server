package com.hanahakdangwebsocketserver.notification.repository;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.hanahakdangwebsocketserver.redis.RedisBoundHash;
import static com.hanahakdangwebsocketserver.redis.RedisExceptionEnum.ERROR_DURING_OPERATION;

@Log4j2
@Repository
public class EmitterRepositoryImpl implements EmitterRepository {
  /**
   * HTTP 1.1, 2.0 버전에서는 브라우저당 여러 개의 SSE 커넥션을 유지할 수 있기 때문에 동일 사용자가 사용하는 서로 다른 Emitter들도 구분할 필요가 있다.
   * 이를 위해 Redis와 ConcurrentHashMap으로 관리한다.
   */

  private static final String SSE_USERS_KEY = "sse-users";

  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>(); // 실제 SSE 객체 관리

  private final RedisBoundHash<String> userEmittersRedisHash;
  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  public EmitterRepositoryImpl(RedisTemplate<String, String> redisTemplate,
      ObjectMapper objectMapper) {
    this.userEmittersRedisHash = new RedisBoundHash<>(SSE_USERS_KEY, redisTemplate,
        objectMapper);
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  private HashSet<String> getEmittersSetFromRedis(Long userId) throws JsonProcessingException {
    String emittersString = userEmittersRedisHash.get(userId.toString(), String.class).orElse(null);
    if (emittersString == null) {
      return new HashSet<>();
    }
    return objectMapper.readValue(emittersString, new TypeReference<HashSet<String>>() {
    });
  }

  private void removeUserEmittersFromRedis(Long userId) {
    userEmittersRedisHash.delete(userId.toString());
  }

  @Override
  public SseEmitter saveEmitter(Long userId, String emitterId, SseEmitter emitter) {
    try {
      HashSet<String> userEmittersSet = getEmittersSetFromRedis(userId);
      userEmittersSet.add(emitterId);
      userEmittersRedisHash.put(userId.toString(), userEmittersSet.toString());
    } catch (JsonProcessingException e) {
      log.error(ERROR_DURING_OPERATION.getMessage());
    }

    emitters.put(emitterId, emitter);

    log.info("Saved emitterId {} for userId {} in Redis Hash", emitterId, userId);

    return emitter;
  }

  @Override
  public Map<String, SseEmitter> findEmittersByUserId(Long userId) {

    Map<String, SseEmitter> userEmittersMap = new ConcurrentHashMap<>();

    try {
      HashSet<String> userEmittersSet = getEmittersSetFromRedis(userId);
      userEmittersSet.forEach(emitterId -> {
        SseEmitter emitter = emitters.get(emitterId);
        if (emitter != null) {
          userEmittersMap.put(emitterId, emitter);
        }
      });
    } catch (JsonProcessingException e) {
      log.error(ERROR_DURING_OPERATION.getMessage());
    }

    return userEmittersMap;
  }

  @Override
  public void deleteEmitter(Long userId, String emitterId) {
    try {
      HashSet<String> userEmittersSet = getEmittersSetFromRedis(userId);
      userEmittersSet.remove(emitterId);
      userEmittersRedisHash.put(userId.toString(), userEmittersSet.toString());
    } catch (JsonProcessingException e) {
      log.error(ERROR_DURING_OPERATION.getMessage());
    }

    emitters.remove(emitterId);

    log.info("Deleted emitterId {} for userId {}", emitterId, userId);
  }

  @Override
  public void deleteAllEmittersByUserId(Long userId) {
    try {
      HashSet<String> userEmittersSet = getEmittersSetFromRedis(userId);
      userEmittersSet.forEach(emitters::remove); // emitter 객체 삭제
      removeUserEmittersFromRedis(userId); // 레디스에서도 제거
    } catch (JsonProcessingException e) {
      log.error(ERROR_DURING_OPERATION.getMessage());
    }

    log.info("Deleted all emitters for userId {}", userId);
  }
}

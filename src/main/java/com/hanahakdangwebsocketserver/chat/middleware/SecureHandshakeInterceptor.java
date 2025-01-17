package com.hanahakdangwebsocketserver.chat.middleware;

import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.hanahakdangwebsocketserver.redis.RedisBoundHash;
import static com.hanahakdangwebsocketserver.chat.enums.ChatExceptionEnum.INVALID_CLASSROOM_PWD;
import static com.hanahakdangwebsocketserver.chat.enums.ChatExceptionEnum.NOT_FOUND_CLASSROOM;


/**
 * 웹소켓 handshake 전에 필요한 인증을 위한 인터셉터
 */
@Log4j2
@Component
public class SecureHandshakeInterceptor implements HandshakeInterceptor {

  @Autowired
  private RedisBoundHash<String> redisStringBoundHash;


  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    log.debug("Before handshake...");
    // TODO: 핸드쉐이크할 때 강의실 비밀번호를 어디에 넣을지 결정 이후에 구현
    try {
      String queryStr = request.getURI().getQuery();
      String password = "";
      log.debug(queryStr);
      String savedPassword = redisStringBoundHash.get("", String.class).orElseThrow(
          NOT_FOUND_CLASSROOM::createRuntimeException);
      if (!savedPassword.equals(password)) {
        throw INVALID_CLASSROOM_PWD.createRuntimeException();
      }

    } catch (RuntimeException e) {
      log.warn(e.getMessage());
//      TODO: 구현 이후에 false로 변경 필요
      return true;
    }

    return true;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception exception) {
  }

}

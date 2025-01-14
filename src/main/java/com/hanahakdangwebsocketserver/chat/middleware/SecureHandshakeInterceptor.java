package com.hanahakdangwebsocketserver.chat.middleware;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.hanahakdangwebsocketserver.chat.service.ClassroomCheckService;


/**
 * 웹소켓 handshake 전에 필요한 인증을 위한 인터셉터
 */
@Log4j2
@RequiredArgsConstructor
public class SecureHandshakeInterceptor implements HandshakeInterceptor {

  private final ClassroomCheckService classroomCheckService;

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    log.info("Before handshake...");
    log.info(request.getURI().getQuery());
    log.info(attributes.toString());
    return true;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception exception) {
  }

}

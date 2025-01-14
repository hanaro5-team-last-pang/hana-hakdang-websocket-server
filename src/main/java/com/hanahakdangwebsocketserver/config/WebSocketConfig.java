package com.hanahakdangwebsocketserver.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.hanahakdangwebsocketserver.chat.middleware.SecureHandshakeInterceptor;
import com.hanahakdangwebsocketserver.chat.service.ClassroomCheckService;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Bean
  public ClassroomCheckService classroomCheckService() {
    return new ClassroomCheckService();
  }

  @Bean
  public SecureHandshakeInterceptor secureHandshakeInterceptor() {
    return new SecureHandshakeInterceptor(classroomCheckService());
  }
  
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/classroom")
        .addInterceptors(secureHandshakeInterceptor())
        .setAllowedOrigins("*");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic");
    registry.setApplicationDestinationPrefixes("/app");
  }

}

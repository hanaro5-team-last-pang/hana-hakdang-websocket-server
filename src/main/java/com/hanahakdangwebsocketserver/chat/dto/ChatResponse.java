package com.hanahakdangwebsocketserver.chat.dto;


import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 웹소켓을 통해 나갈 채팅 응답 타입
 */
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatResponse {

  private LocalDateTime timestamp;
  private String username;
  private String body;
}

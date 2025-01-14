package com.hanahakdangwebsocketserver.chat.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 웹소켓을 통해 들어오는 채팅 요청 타입
 */
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRequest {

  @NotNull
  @NotBlank
  private String username;

  @NotNull
  private String body;
}

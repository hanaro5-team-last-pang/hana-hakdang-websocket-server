package com.hanahakdangwebsocketserver.chat.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatExceptionEnum {
  NOT_FOUND_CLASSROOM("강의실 정보를 찾을 수 없습니다."),
  INVALID_CLASSROOM_PWD("잘못된 강의실 비밀번호입니다.");

  private final String message;

  public RuntimeException createRuntimeException() {
    return new RuntimeException(message);
  }
}
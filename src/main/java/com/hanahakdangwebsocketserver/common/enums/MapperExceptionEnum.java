package com.hanahakdangwebsocketserver.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum MapperExceptionEnum {
  CANT_CONVERT_INTO_JSON("json으로 변환할 수 없습니다."),
  CANT_CONVERT_INTO_OBJ("객체로 변환할 수 없습니다.");

  private final String message;

}

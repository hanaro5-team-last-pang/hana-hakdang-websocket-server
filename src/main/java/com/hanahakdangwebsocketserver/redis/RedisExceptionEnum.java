package com.hanahakdangwebsocketserver.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisExceptionEnum {
  ERROR_DURING_OPERATION("작업 중 오류가 발생했습니다."),
  CANT_GET_VALUE("value를 가져올 수 없습니다."),
  CANT_DELETE_BY_KEY("key를 통해 삭제할 수 없습니다."),
  CANT_CONVERT_INTO_JSON("json으로 변환할 수 없습니다."),
  CANT_CONVERT_INTO_DTO("객체로 변환할 수 없습니다."),
  CANT_DESERIALIZE_JSON("JSON 역직렬화 중 오류가 발생했습니다."); // 새 메시지 추가


  private final String message;
}

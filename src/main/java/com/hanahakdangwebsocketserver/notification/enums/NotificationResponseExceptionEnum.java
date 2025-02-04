package com.hanahakdangwebsocketserver.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
@AllArgsConstructor
public enum NotificationResponseExceptionEnum {
  NOT_VALID_USERID(HttpStatus.BAD_REQUEST, "유요하지 않은 유저 아이디입니다."),
  UNAUTHORIZED_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");

  private final HttpStatus httpStatus;
  private final String message;

  public ResponseStatusException createResponseStatusException() {
    return new ResponseStatusException(this.httpStatus, this.message);
  }
}

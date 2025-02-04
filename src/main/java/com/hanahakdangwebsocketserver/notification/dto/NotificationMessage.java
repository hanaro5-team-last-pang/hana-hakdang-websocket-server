package com.hanahakdangwebsocketserver.notification.dto;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 카프카에 발행(publish)될 메시지 타입을 정의
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class NotificationMessage {

  private Long notificationId;

  private Long receiverId;

  private String type;

  private String content;

  private Boolean isSeen;

  private LocalDateTime createdAt;

}

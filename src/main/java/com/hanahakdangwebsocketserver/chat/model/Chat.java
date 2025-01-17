package com.hanahakdangwebsocketserver.chat.model;


import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * 레디스 List에 저장될 원소 모델
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Chat implements Serializable {

  private Long lectureId;

  private String username;

  private String body;

  /**
   * {@link java.time.LocalDateTime}을 직렬화/역직렬화하기 위해서 다음과 같은 애너테이션들이 필요
   */
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();
}

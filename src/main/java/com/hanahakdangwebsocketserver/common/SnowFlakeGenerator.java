package com.hanahakdangwebsocketserver.common;

import java.time.Instant;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class SnowFlakeGenerator {

  private static final int CASE_ONE_BITS = 10;
  private static final int CASE_TWO_BITS = 9;
  private static final int SEQUENCE_BITS = 4; // 중복 방지용

  private static final int maxSequence = (int) (Math.pow(2, CASE_ONE_BITS) - 1); // 2^5-1

  private static final long CUSTOM_EPOCH = 1735657200000L; // 2025-01-01 00:00:00; 41bit
  private volatile long sequence = 0L;
  private int case_one = 10;
  private int case_two = 0;
  private volatile long lastTimestamp = -1L;

  private static long timestamp() {
    return Instant.now().toEpochMilli() - CUSTOM_EPOCH; // 41비트를 맞춰주기 위함
  }

  public synchronized long nextId() {
    long currentTimestamp = timestamp();

    if (currentTimestamp < lastTimestamp) {
      throw new IllegalStateException("Invalid System Clock!");
    }

    // 동일한 시간에 들어온 요청으로 판단
    if (currentTimestamp == lastTimestamp) {
      sequence = (sequence + 1) & maxSequence;
      if (sequence == 0) {
        currentTimestamp = waitNextMillis(currentTimestamp);
      }
    } else {
      sequence = 0;
    }

    lastTimestamp = currentTimestamp;
    return makeId(currentTimestamp);
  }

  private Long makeId(long currentTimestamp) {
    long id = 0;

    id |= (currentTimestamp << CASE_ONE_BITS + CASE_TWO_BITS + SEQUENCE_BITS);
    id |= (case_one << CASE_TWO_BITS + SEQUENCE_BITS);
    id |= (case_two << SEQUENCE_BITS);
    id |= sequence;

    return id;
  }

  private long waitNextMillis(long currentTimestamp) {
    while (currentTimestamp == lastTimestamp) {
      currentTimestamp = timestamp();
    }
    return currentTimestamp;
  }
}

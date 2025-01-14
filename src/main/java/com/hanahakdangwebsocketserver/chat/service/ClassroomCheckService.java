package com.hanahakdangwebsocketserver.chat.service;


import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import com.hanahakdangwebsocketserver.chat.model.Classroom;


@Service
public class ClassroomCheckService {

  @Value("${classroom.key}")
  private String classroomKey;

  @Resource(name = "redisTemplate")
  private SetOperations<String, Classroom> setOpts;

  public boolean checkClassroom(Classroom classroom) {
    //TODO: 강의실 접속 가능 여부 확인 로직 추가 필요
    return true;
  }
}

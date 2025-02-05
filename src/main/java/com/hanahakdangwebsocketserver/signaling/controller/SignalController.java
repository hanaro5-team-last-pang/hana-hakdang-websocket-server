package com.hanahakdangwebsocketserver.signaling.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;


@Log4j2
@RestController
@RequiredArgsConstructor
public class SignalController {

  private final SimpMessagingTemplate simpMessagingTemplate;

  @MessageMapping("/signaling/{classroomId}")
  public void receiveSignal(@DestinationVariable("classroomId") String classroomId,
      Message<?> message) {
    log.debug("Received signal {} from {}", message, classroomId);
    simpMessagingTemplate.convertAndSend("/topic/signaling/" + classroomId, message.getPayload());
  }

  @MessageMapping("/answer/{classroomId}")
  public void receiveAnswer(@DestinationVariable("classroomId") String classroomId,
      Message<?> message) {
    log.debug("Received Answer {} from {}", message, classroomId);
    simpMessagingTemplate.convertAndSend("/topic/answer/" + classroomId, message.getPayload());
  }

  @MessageMapping("/trickle/{classroomId}")
  public void trickleIce(@DestinationVariable("classroomId") String classroomId,
      Message<?> message) {
    log.debug("Received trickle {} from {}", message, classroomId);
    simpMessagingTemplate.convertAndSend("/topic/trickle/" + classroomId, message.getPayload());
  }

  @MessageMapping("/enter/{classroomId}")
  public void enter(@DestinationVariable("classroomId") String classroomId, Message<?> message) {
    log.debug("Received enter {} from {}", message, classroomId);
    simpMessagingTemplate.convertAndSend("/topic/enter/" + classroomId, message.getPayload());
  }
}

package com.khoi.ask.controller;

import com.khoi.ask.dto.Message;
import com.khoi.ask.dto.OutputMessage;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class AskSocketController {

  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;
  private static final Logger log = LoggerFactory.getLogger(AskSocketController.class);

  @MessageMapping("/chat")
  @SendTo("/history")
  public OutputMessage sendAll(Message msg) throws Exception {
    OutputMessage out = new OutputMessage(msg.getFrom(), msg.getText(),
        new SimpleDateFormat("HH:mm").format(new Date()));
    return out;
  }

  @MessageMapping("/room")
  public void sendSpecific(@Payload Message msg, Principal user,
      @Header("simpSessionId") String sessionId) throws Exception {
    OutputMessage out = new OutputMessage(msg.getFrom(), msg.getText(),
        new SimpleDateFormat("HH:mm").format(new Date()));
    simpMessagingTemplate.convertAndSendToUser(msg.getTo(), "/user/queue/specific-user", out);
  }
}

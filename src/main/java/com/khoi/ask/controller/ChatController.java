package com.khoi.ask.controller;

import static java.lang.String.format;

import com.khoi.ask.model.ChatMessage;
import com.khoi.ask.model.ChatMessage.MessageType;
import com.khoi.ask.service.IAskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
  @Autowired
  IAskService askService;
  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @MessageMapping("/chat/{roomId}/sendMessage")
  public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
    messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
  }

  @MessageMapping("/chat/{roomId}/addUser")
  public void addUser(
      @DestinationVariable String roomId,
      @Payload ChatMessage chatMessage,
      SimpMessageHeaderAccessor headerAccessor) {
    String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", roomId);
    if (currentRoomId != null) {
      ChatMessage leaveMessage = new ChatMessage();
      leaveMessage.setType(MessageType.LEAVE);
      leaveMessage.setSender(chatMessage.getSender());
      messagingTemplate.convertAndSend(format("/channel/%s", currentRoomId), leaveMessage);
    }
    headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
  }

  @MessageMapping("/chat/{roomId}/{productId}/info")
  public void getInfo(
      @DestinationVariable String roomId,
      @DestinationVariable String productId,
      @Payload ChatMessage chatMessage,
      SimpMessageHeaderAccessor headerAccessor) {
    headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    chatMessage.setContent("room Id: " + roomId + " product Id: " + productId);
    messagingTemplate.convertAndSend(format("/lobby"), chatMessage);
  }

  @MessageMapping("/chat/agent/{roomId}/sendMessage")
  public void sendAgentMessage(
      @DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
    messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
  }

  @MessageMapping("/chat/agent/{roomId}/addAgent")
  public void addAgent(
      @DestinationVariable String roomId,
      @Payload ChatMessage chatMessage,
      SimpMessageHeaderAccessor headerAccessor) {
    String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", roomId);
    if (currentRoomId != null) {
      ChatMessage leaveMessage = new ChatMessage();
      leaveMessage.setType(MessageType.LEAVE);
      leaveMessage.setSender(chatMessage.getSender());
      messagingTemplate.convertAndSend(format("/channel/%s", currentRoomId), leaveMessage);
    }
    headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
  }

  @MessageMapping("/chat/agent/{roomId}/getProductInfo")
  public void getProductInfo(
      @DestinationVariable String roomId,
      @Payload ChatMessage chatMessage,
      SimpMessageHeaderAccessor headerAccessor) {
    if (chatMessage.getType() == MessageType.PRODUCT) {
      headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
      chatMessage.setContent(askService.getProductInfo(
          chatMessage.getContent() != null ? Integer.parseInt(chatMessage.getContent()) : -1));
      messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
    }
  }
}
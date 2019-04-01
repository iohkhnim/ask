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

  /**
   * <p>This controller receives every message sent to /chat/{roomId}/sendMessage then sends to
   * everyone subscribes to /channel/{roomId} </p>
   *
   * @param roomId Id of current room
   * @param chatMessage Contains CHAT message
   */
  @MessageMapping("/chat/{roomId}/sendMessage")
  public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
    messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
  }

  /**
   * <p>This controller receives every message sent to /chat/{roomId}/addUser then sends to
   * everyone subscribes to /channel/{roomId} </p>
   *
   * @param roomId Id of current room
   * @param chatMessage Contains JOIN message
   * @param headerAccessor Access to Header
   */
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

  /**
   * <p>This controller receives message from user then announces to agent user's room ID and
   * product ID</p>
   *
   * @param roomId Id of user's room
   * @param productId Id of product that customer wants to talk about
   * @param chatMessage Contains INFO message
   * @param headerAccessor Access to Header
   */
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

  /**
   * <p>This controller receives every message sent to /chat/agent/{roomId}/sendMessage then sends
   * to everyone subscribes to /channel/{roomId} </p>
   *
   * @param roomId Id of current room
   * @param chatMessage Contains CHAT message
   */
  @MessageMapping("/chat/agent/{roomId}/sendMessage")
  public void sendAgentMessage(
      @DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
    messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
  }

  /**
   * <p>This controller receives every message sent to /chat/{roomId}/addAgent then sends to
   * everyone
   * subscribes to /channel/{roomId} </p>
   *
   * @param roomId Id of current room
   * @param chatMessage Contains JOIN message
   * @param headerAccessor Access to Header
   */
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

  /**
   * <p>This controller receives every message sent to /chat/agent/{roomId}/getProductInfo then
   * sends to agent product information</p>
   *
   * @param roomId Id of current room
   * @param chatMessage Contains PRODUCT message which has product information
   * @param headerAccessor Access to Header
   */
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

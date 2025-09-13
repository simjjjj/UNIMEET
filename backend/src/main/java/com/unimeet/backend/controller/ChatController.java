package com.unimeet.backend.controller;

import com.unimeet.backend.domain.ChatMessage;
import com.unimeet.backend.domain.ChatRoom;
import com.unimeet.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:19006"})
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, 
                           @Payload ChatMessage chatMessage,
                           SimpMessageHeaderAccessor headerAccessor) {
        
        // 사용자 정보 설정 (JWT에서 추출 가능)
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        
        chatMessage.setSender(username != null ? username : "Anonymous");
        chatMessage.setSenderId(userId);
        chatMessage.setRoomId(roomId);
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        
        // 메시지 저장
        ChatMessage savedMessage = chatService.saveMessage(chatMessage);
        
        // 메시지를 해당 채팅방의 모든 구독자에게 전송
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);
    }

    @MessageMapping("/chat/{roomId}/addUser")
    public void addUser(@DestinationVariable String roomId,
                       @Payload ChatMessage chatMessage,
                       SimpMessageHeaderAccessor headerAccessor) {
        
        // 세션에 사용자 정보 저장
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("userId", chatMessage.getSenderId());
        headerAccessor.getSessionAttributes().put("roomId", roomId);
        
        // 사용자 참가 메시지 설정
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setRoomId(roomId);
        chatMessage.setContent(chatMessage.getSender() + " joined the chat");
        
        // 참가 메시지 저장
        ChatMessage savedMessage = chatService.saveMessage(chatMessage);
        
        // 사용자 참가 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);
    }
}

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:19006"})
class ChatRestController {

    private final ChatService chatService;

    /**
     * 채팅방 생성 또는 조회
     */
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> getOrCreateChatRoom(@RequestBody Map<String, String> request) {
        String userAId = request.get("userAId");
        String userBId = request.get("userBId");
        
        if (userAId == null || userBId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        ChatRoom chatRoom = chatService.getOrCreateChatRoom(userAId, userBId);
        return ResponseEntity.ok(chatRoom);
    }

    /**
     * 채팅방 메시지 목록 조회
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable String roomId) {
        List<ChatMessage> messages = chatService.getChatMessages(roomId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 사용자의 채팅방 목록 조회
     */
    @GetMapping("/rooms/user/{userId}")
    public ResponseEntity<List<ChatRoom>> getUserChatRooms(@PathVariable String userId) {
        List<ChatRoom> chatRooms = chatService.getUserChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }

    /**
     * 메시지 읽음 처리
     */
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable String roomId, 
                                               @RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }
        
        chatService.markMessagesAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 읽지 않은 메시지 수 조회
     */
    @GetMapping("/rooms/{roomId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(@PathVariable String roomId,
                                                                  @RequestParam String userId) {
        long count = chatService.getUnreadMessageCount(roomId, userId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}


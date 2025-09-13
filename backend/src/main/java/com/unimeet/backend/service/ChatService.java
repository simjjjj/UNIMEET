package com.unimeet.backend.service;

import com.unimeet.backend.domain.ChatMessage;
import com.unimeet.backend.domain.ChatRoom;
import com.unimeet.backend.repository.ChatMessageRepository;
import com.unimeet.backend.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 채팅방 생성 또는 조회
     */
    public ChatRoom getOrCreateChatRoom(String userAId, String userBId) {
        // 기존 채팅방 확인
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByParticipantIdsContainingAllIgnoreOrder(
                List.of(userAId, userBId));
        
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        // 새 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .participantIds(List.of(userAId, userBId))
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    /**
     * 메시지 저장
     */
    public ChatMessage saveMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        return chatMessageRepository.save(message);
    }

    /**
     * 채팅방의 메시지 목록 조회
     */
    public List<ChatMessage> getChatMessages(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }

    /**
     * 사용자의 채팅방 목록 조회
     */
    public List<ChatRoom> getUserChatRooms(String userId) {
        return chatRoomRepository.findByParticipantIdsContainingAndActiveTrue(userId);
    }

    /**
     * 메시지 읽음 처리
     */
    public void markMessagesAsRead(String roomId, String userId) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findByRoomIdAndReadFalseAndSenderIdNot(roomId, userId);
        unreadMessages.forEach(message -> message.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);
    }

    /**
     * 읽지 않은 메시지 수 조회
     */
    public long getUnreadMessageCount(String roomId, String userId) {
        return chatMessageRepository.countByRoomIdAndReadFalseAndSenderIdNot(roomId, userId);
    }

    /**
     * 채팅방 비활성화
     */
    public void deactivateChatRoom(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        chatRoom.setActive(false);
        chatRoomRepository.save(chatRoom);
    }
}
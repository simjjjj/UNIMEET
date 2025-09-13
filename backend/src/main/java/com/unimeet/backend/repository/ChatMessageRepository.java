package com.unimeet.backend.repository;

import com.unimeet.backend.domain.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
    List<ChatMessage> findByRoomIdAndReadFalse(String roomId);
    List<ChatMessage> findByRoomIdAndReadFalseAndSenderIdNot(String roomId, String senderId);
    long countByRoomIdAndReadFalseAndSenderIdNot(String roomId, String senderId);
} 
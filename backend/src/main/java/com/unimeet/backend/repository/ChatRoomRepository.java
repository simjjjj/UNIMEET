package com.unimeet.backend.repository;

import com.unimeet.backend.domain.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    List<ChatRoom> findByParticipantIdsContaining(String userId);
    List<ChatRoom> findByParticipantIdsContainingAndActiveTrue(String userId);
    boolean existsByParticipantIdsContaining(String userId);
    
    @Query("{ 'participantIds': { $all: ?0 } }")
    Optional<ChatRoom> findByParticipantIdsContainingAllIgnoreOrder(List<String> participantIds);
} 
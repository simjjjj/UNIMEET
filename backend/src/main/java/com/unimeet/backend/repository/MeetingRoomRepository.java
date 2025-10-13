package com.unimeet.backend.repository;

import com.unimeet.backend.domain.MeetingRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRoomRepository extends MongoRepository<MeetingRoom, String> {
    
    List<MeetingRoom> findByCreatorIdOrderByCreatedAtDesc(String creatorId);
    List<MeetingRoom> findByActiveOrderByCreatedAtDesc(boolean active);
    
    @Query("{'participants.userId': ?0}")
    List<MeetingRoom> findByParticipantUserId(String userId);
    
    List<MeetingRoom> findByTypeAndActiveOrderByCreatedAtDesc(MeetingRoom.MeetingType type, boolean active);
    
    @Query("{'active': true, '$expr': {'$lt': [{'$size': '$participants'}, '$maxParticipants']}}")
    List<MeetingRoom> findAvailableRooms();
}

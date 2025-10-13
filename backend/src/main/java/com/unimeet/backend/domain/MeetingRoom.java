package com.unimeet.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "meeting_rooms")
public class MeetingRoom {
    
    @Id
    private String id;
    
    private String title;
    private String description;
    private MeetingType type;
    private List<Participant> participants;
    private String creatorId;
    private boolean active;
    private int maxParticipants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Participant {
        private String userId;
        private String name;
        private String nickname;
        private String gender;
        private String department;
        private int age;
        private String studentId;
        private String mbti;
        private List<String> interests;
        private LocalDateTime joinedAt;
        private boolean isCreator;
    }
    
    public enum MeetingType {
        PAIR,
        MIXED
    }
}

package com.unimeet.backend.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "chat_rooms")
public class ChatRoom {
    @Id
    private String id;
    
    private List<String> participantIds;
    private LocalDateTime createdAt;
    private boolean active;
} 



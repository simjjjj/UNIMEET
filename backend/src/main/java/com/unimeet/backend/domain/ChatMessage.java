package com.unimeet.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String id;
    
    private String roomId;
    private String sender;
    private String senderId;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;
    private boolean read;
    
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
} 
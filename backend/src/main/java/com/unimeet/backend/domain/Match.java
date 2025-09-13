package com.unimeet.backend.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "matches")
public class Match {
    @Id
    private String id;
    
    private String userAId;
    private String userBId;
    private double score;
    private LocalDateTime matchedAt;
    private MatchStatus status;
    
    public enum MatchStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        EXPIRED
    }
} 
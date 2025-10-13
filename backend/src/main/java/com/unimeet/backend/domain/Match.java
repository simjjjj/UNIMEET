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
@Document(collection = "matches")
public class Match {
    @Id
    private String id;
    
    private String userAId;              // 사용자 A ID (기존 매칭용)
    private String userBId;              // 사용자 B ID (기존 매칭용)
    private String requesterId;          // 매칭 요청자 ID (새 매칭 시스템용)
    private String targetId;             // 매칭 대상자 ID (새 매칭 시스템용)
    private double score;                // 호환성 점수
    private double compatibilityScore;   // 호환성 점수 (새 매칭 시스템용)
    private String message;              // 매칭 요청 메시지
    private LocalDateTime matchedAt;     // 매칭 시간
    private LocalDateTime createdAt;     // 생성 시간
    private LocalDateTime updatedAt;     // 수정 시간
    private LocalDateTime respondedAt;   // 응답 시간
    private MatchStatus status;
    
    public enum MatchStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        EXPIRED
    }
} 
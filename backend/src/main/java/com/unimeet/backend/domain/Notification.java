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
@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    private String userId;              // 알림 받을 사용자 ID
    private NotificationType type;      // 알림 타입
    private String title;               // 알림 제목
    private String message;             // 알림 내용
    private String relatedId;           // 관련 ID (매칭 ID, 채팅방 ID 등)
    private boolean isRead;             // 읽음 여부
    private LocalDateTime createdAt;    // 생성 시간
    private LocalDateTime readAt;       // 읽은 시간
    
    public enum NotificationType {
        MATCH_REQUEST,      // 매칭 요청
        MATCH_ACCEPTED,     // 매칭 수락
        MATCH_REJECTED,     // 매칭 거절
        NEW_MESSAGE,        // 새 메시지
        MEETING_INVITE,     // 미팅방 초대
        MEETING_JOINED,     // 미팅방 참가
        SYSTEM              // 시스템 알림
    }
}

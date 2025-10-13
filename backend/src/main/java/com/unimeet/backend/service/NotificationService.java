package com.unimeet.backend.service;

import com.unimeet.backend.domain.Notification;
import com.unimeet.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * 알림 생성 및 전송
     */
    @Transactional
    public Notification createNotification(String userId, Notification.NotificationType type, 
                                         String title, String message, String relatedId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .relatedId(relatedId)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        Notification saved = notificationRepository.save(notification);
        
        // WebSocket으로 실시간 알림 전송
        sendRealTimeNotification(userId, saved);
        
        log.info("알림 생성: {} -> {}", type, userId);
        return saved;
    }
    
    /**
     * 매칭 요청 알림
     */
    public void sendMatchRequestNotification(String targetUserId, String requesterName, String matchId) {
        createNotification(
            targetUserId,
            Notification.NotificationType.MATCH_REQUEST,
            "새로운 매칭 요청",
            requesterName + "님이 매칭을 요청했습니다",
            matchId
        );
    }
    
    /**
     * 매칭 수락 알림
     */
    public void sendMatchAcceptedNotification(String requesterUserId, String accepterName, String matchId) {
        createNotification(
            requesterUserId,
            Notification.NotificationType.MATCH_ACCEPTED,
            "매칭 수락",
            accepterName + "님이 매칭을 수락했습니다",
            matchId
        );
    }
    
    /**
     * 매칭 거절 알림
     */
    public void sendMatchRejectedNotification(String requesterUserId, String rejecterName, String matchId) {
        createNotification(
            requesterUserId,
            Notification.NotificationType.MATCH_REJECTED,
            "매칭 거절",
            rejecterName + "님이 매칭을 거절했습니다",
            matchId
        );
    }
    
    /**
     * 새 메시지 알림
     */
    public void sendNewMessageNotification(String userId, String senderName, String chatRoomId) {
        createNotification(
            userId,
            Notification.NotificationType.NEW_MESSAGE,
            "새 메시지",
            senderName + "님이 메시지를 보냈습니다",
            chatRoomId
        );
    }
    
    /**
     * 미팅방 참가 알림
     */
    public void sendMeetingJoinedNotification(String creatorId, String joinerName, String meetingRoomId) {
        createNotification(
            creatorId,
            Notification.NotificationType.MEETING_JOINED,
            "미팅방 참가",
            joinerName + "님이 미팅방에 참가했습니다",
            meetingRoomId
        );
    }
    
    /**
     * 사용자 알림 목록 조회
     */
    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * 읽지 않은 알림 조회
     */
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
    
    /**
     * 읽지 않은 알림 개수
     */
    public long getUnreadNotificationCount(String userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    /**
     * 알림 읽음 처리
     */
    @Transactional
    public Notification markAsRead(String notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
        
        // 본인의 알림인지 확인
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("알림을 읽을 권한이 없습니다");
        }
        
        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            return notificationRepository.save(notification);
        }
        
        return notification;
    }
    
    /**
     * 모든 알림 읽음 처리
     */
    @Transactional
    public void markAllAsRead(String userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
        
        notificationRepository.saveAll(unreadNotifications);
        log.info("모든 알림 읽음 처리: {} ({}개)", userId, unreadNotifications.size());
    }
    
    /**
     * 알림 삭제
     */
    @Transactional
    public void deleteNotification(String notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
        
        // 본인의 알림인지 확인
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("알림을 삭제할 권한이 없습니다");
        }
        
        notificationRepository.delete(notification);
        log.info("알림 삭제: {}", notificationId);
    }
    
    /**
     * WebSocket으로 실시간 알림 전송
     */
    private void sendRealTimeNotification(String userId, Notification notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                notification
            );
            log.debug("실시간 알림 전송: {} -> {}", notification.getType(), userId);
        } catch (Exception e) {
            log.warn("실시간 알림 전송 실패: {}", e.getMessage());
        }
    }
}

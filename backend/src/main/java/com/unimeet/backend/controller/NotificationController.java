package com.unimeet.backend.controller;

import com.unimeet.backend.domain.Notification;
import com.unimeet.backend.domain.User;
import com.unimeet.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    /**
     * 내 알림 목록 조회
     */
    @GetMapping
    public ResponseEntity<?> getMyNotifications() {
        try {
            String userId = getCurrentUserId();
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "알림 목록",
                "notifications", notifications
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "알림 조회 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 읽지 않은 알림 조회
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications() {
        try {
            String userId = getCurrentUserId();
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            long unreadCount = notificationService.getUnreadNotificationCount(userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "읽지 않은 알림",
                "notifications", notifications,
                "unreadCount", unreadCount
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "알림 조회 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 읽지 않은 알림 개수
     */
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadCount() {
        try {
            String userId = getCurrentUserId();
            long count = notificationService.getUnreadNotificationCount(userId);
            
            return ResponseEntity.ok(Map.of(
                "unreadCount", count
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "알림 개수 조회 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 알림 읽음 처리
     */
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String notificationId) {
        try {
            String userId = getCurrentUserId();
            Notification notification = notificationService.markAsRead(notificationId, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "알림을 읽었습니다",
                "notification", notification
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                "error", "알림 읽음 처리 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 모든 알림 읽음 처리
     */
    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        try {
            String userId = getCurrentUserId();
            notificationService.markAllAsRead(userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "모든 알림을 읽었습니다"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "알림 읽음 처리 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 알림 삭제
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable String notificationId) {
        try {
            String userId = getCurrentUserId();
            notificationService.deleteNotification(notificationId, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "알림을 삭제했습니다"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                "error", "알림 삭제 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 현재 로그인한 사용자 ID 조회
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user.getId();
        }
        throw new RuntimeException("User not authenticated");
    }
}

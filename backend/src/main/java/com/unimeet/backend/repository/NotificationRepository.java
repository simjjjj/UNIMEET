package com.unimeet.backend.repository;

import com.unimeet.backend.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    // 사용자별 알림 조회 (최신순)
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    
    // 읽지 않은 알림 조회
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(String userId);
    
    // 읽지 않은 알림 개수
    long countByUserIdAndIsReadFalse(String userId);
    
    // 특정 타입의 알림 조회
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(String userId, Notification.NotificationType type);
    
    // 관련 ID로 알림 조회
    List<Notification> findByRelatedId(String relatedId);
}

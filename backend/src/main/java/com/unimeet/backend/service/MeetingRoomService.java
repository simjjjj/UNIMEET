package com.unimeet.backend.service;

import com.unimeet.backend.domain.MeetingRoom;
import com.unimeet.backend.domain.User;
import com.unimeet.backend.exception.UserNotFoundException;
import com.unimeet.backend.repository.MeetingRoomRepository;
import com.unimeet.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingRoomService {
    
    private final MeetingRoomRepository meetingRoomRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    @Transactional
    public MeetingRoom createMeetingRoom(String creatorId, String title, String description, 
                                       MeetingRoom.MeetingType type, int maxParticipants) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new UserNotFoundException("Creator not found: " + creatorId));
        
        MeetingRoom.Participant creatorParticipant = MeetingRoom.Participant.builder()
                .userId(creator.getId())
                .name(creator.getName())
                .nickname(creator.getNickname())
                .gender(creator.getGender())
                .department(creator.getDepartment())
                .age(calculateAge(creator.getBirth()))
                .studentId(creator.getStudentId())
                .mbti(creator.getMbti())
                .interests(creator.getInterests())
                .joinedAt(LocalDateTime.now())
                .isCreator(true)
                .build();
        
        List<MeetingRoom.Participant> participants = new ArrayList<>();
        participants.add(creatorParticipant);
        
        MeetingRoom meetingRoom = MeetingRoom.builder()
                .title(title)
                .description(description)
                .type(type)
                .participants(participants)
                .creatorId(creatorId)
                .active(true)
                .maxParticipants(maxParticipants)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        MeetingRoom saved = meetingRoomRepository.save(meetingRoom);
        log.info("미팅방 생성: {} by {}", saved.getId(), creatorId);
        
        return saved;
    }
    
    @Transactional
    public MeetingRoom joinMeetingRoom(String roomId, String userId) {
        MeetingRoom room = meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Meeting room not found: " + roomId));
        
        boolean alreadyJoined = room.getParticipants().stream()
                .anyMatch(p -> p.getUserId().equals(userId));
        
        if (alreadyJoined) {
            throw new RuntimeException("이미 참가한 미팅방입니다");
        }
        
        if (room.getParticipants().size() >= room.getMaxParticipants()) {
            throw new RuntimeException("미팅방이 가득 찼습니다");
        }
        
        if (!room.isActive()) {
            throw new RuntimeException("비활성화된 미팅방입니다");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        
        MeetingRoom.Participant participant = MeetingRoom.Participant.builder()
                .userId(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .department(user.getDepartment())
                .age(calculateAge(user.getBirth()))
                .studentId(user.getStudentId())
                .mbti(user.getMbti())
                .interests(user.getInterests())
                .joinedAt(LocalDateTime.now())
                .isCreator(false)
                .build();
        
        room.getParticipants().add(participant);
        room.setUpdatedAt(LocalDateTime.now());
        
        MeetingRoom saved = meetingRoomRepository.save(room);
        
        // 생성자에게 알림 전송
        notificationService.sendMeetingJoinedNotification(room.getCreatorId(), user.getName(), roomId);
        
        log.info("미팅방 참가: {} joined {}", userId, roomId);
        return saved;
    }
    
    @Transactional
    public MeetingRoom leaveMeetingRoom(String roomId, String userId) {
        MeetingRoom room = meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Meeting room not found: " + roomId));
        
        boolean removed = room.getParticipants().removeIf(p -> p.getUserId().equals(userId));
        
        if (!removed) {
            throw new RuntimeException("미팅방에 참가하지 않은 사용자입니다");
        }
        
        if (room.getCreatorId().equals(userId)) {
            room.setActive(false);
            log.info("미팅방 생성자 퇴장으로 비활성화: {}", roomId);
        }
        
        if (room.getParticipants().isEmpty()) {
            room.setActive(false);
            log.info("미팅방 참가자 없음으로 비활성화: {}", roomId);
        }
        
        room.setUpdatedAt(LocalDateTime.now());
        
        MeetingRoom saved = meetingRoomRepository.save(room);
        log.info("미팅방 퇴장: {} left {}", userId, roomId);
        
        return saved;
    }
    
    public MeetingRoom getMeetingRoom(String roomId) {
        return meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Meeting room not found: " + roomId));
    }
    
    public List<MeetingRoom> getMyMeetingRooms(String userId) {
        return meetingRoomRepository.findByParticipantUserId(userId);
    }
    
    public List<MeetingRoom> getAvailableMeetingRooms() {
        return meetingRoomRepository.findAvailableRooms();
    }
    
    public List<MeetingRoom> getMeetingRoomsByType(MeetingRoom.MeetingType type) {
        return meetingRoomRepository.findByTypeAndActiveOrderByCreatedAtDesc(type, true);
    }
    
    @Transactional
    public MeetingRoom deactivateMeetingRoom(String roomId, String userId) {
        MeetingRoom room = meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Meeting room not found: " + roomId));
        
        if (!room.getCreatorId().equals(userId)) {
            throw new RuntimeException("미팅방을 비활성화할 권한이 없습니다");
        }
        
        room.setActive(false);
        room.setUpdatedAt(LocalDateTime.now());
        
        MeetingRoom saved = meetingRoomRepository.save(room);
        log.info("미팅방 비활성화: {} by {}", roomId, userId);
        
        return saved;
    }
    
    private int calculateAge(String birth) {
        if (birth == null || birth.isEmpty()) {
            return 0;
        }
        try {
            String[] parts = birth.split("-");
            if (parts.length >= 1) {
                int birthYear = Integer.parseInt(parts[0]);
                int currentYear = java.time.Year.now().getValue();
                return currentYear - birthYear + 1;
            }
        } catch (Exception e) {
            // 파싱 실패 시 0 반환
        }
        return 0;
    }
}

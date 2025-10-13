package com.unimeet.backend.controller;

import com.unimeet.backend.domain.MeetingRoom;
import com.unimeet.backend.domain.User;
import com.unimeet.backend.service.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingRoomController {
    
    private final MeetingRoomService meetingRoomService;
    
    @PostMapping("/create")
    public ResponseEntity<?> createMeetingRoom(@RequestBody Map<String, Object> request) {
        try {
            String creatorId = getCurrentUserId();
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            String typeStr = (String) request.get("type");
            Integer maxParticipants = (Integer) request.get("maxParticipants");
            
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "미팅방 제목은 필수입니다"
                ));
            }
            
            if (maxParticipants == null || maxParticipants < 2 || maxParticipants > 10) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "참가자 수는 2명 이상 10명 이하여야 합니다"
                ));
            }
            
            MeetingRoom.MeetingType type = MeetingRoom.MeetingType.MIXED;
            if ("PAIR".equalsIgnoreCase(typeStr)) {
                type = MeetingRoom.MeetingType.PAIR;
            }
            
            MeetingRoom meetingRoom = meetingRoomService.createMeetingRoom(
                creatorId, title, description, type, maxParticipants
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "미팅방이 생성되었습니다",
                "meetingRoom", convertToResponse(meetingRoom)
            ));
            
        } catch (Exception e) {
            log.error("미팅방 생성 실패", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "미팅방 생성 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getMeetingRoom(@PathVariable String roomId) {
        try {
            MeetingRoom meetingRoom = meetingRoomService.getMeetingRoom(roomId);
            return ResponseEntity.ok(convertToResponse(meetingRoom));
            
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                "error", "미팅방을 찾을 수 없습니다",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyMeetingRooms() {
        try {
            String userId = getCurrentUserId();
            List<MeetingRoom> meetingRooms = meetingRoomService.getMyMeetingRooms(userId);
            
            List<Map<String, Object>> result = meetingRooms.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return ResponseEntity.ok(Map.of(
                "message", "내 미팅방 목록",
                "meetingRooms", result
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "미팅방 목록 조회 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableMeetingRooms() {
        try {
            List<MeetingRoom> meetingRooms = meetingRoomService.getAvailableMeetingRooms();
            
            List<Map<String, Object>> result = meetingRooms.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return ResponseEntity.ok(Map.of(
                "message", "참가 가능한 미팅방 목록",
                "meetingRooms", result
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "미팅방 목록 조회 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{roomId}/join")
    public ResponseEntity<?> joinMeetingRoom(@PathVariable String roomId) {
        try {
            String userId = getCurrentUserId();
            MeetingRoom meetingRoom = meetingRoomService.joinMeetingRoom(roomId, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "미팅방에 참가했습니다",
                "meetingRoom", convertToResponse(meetingRoom)
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                "error", "미팅방 참가 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<?> leaveMeetingRoom(@PathVariable String roomId) {
        try {
            String userId = getCurrentUserId();
            MeetingRoom meetingRoom = meetingRoomService.leaveMeetingRoom(roomId, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "미팅방에서 나갔습니다",
                "meetingRoom", convertToResponse(meetingRoom)
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                "error", "미팅방 나가기 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{roomId}/deactivate")
    public ResponseEntity<?> deactivateMeetingRoom(@PathVariable String roomId) {
        try {
            String userId = getCurrentUserId();
            MeetingRoom meetingRoom = meetingRoomService.deactivateMeetingRoom(roomId, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "미팅방이 비활성화되었습니다",
                "meetingRoom", convertToResponse(meetingRoom)
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                "error", "미팅방 비활성화 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getMeetingRoomsByType(@PathVariable String type) {
        try {
            MeetingRoom.MeetingType meetingType;
            if ("pair".equalsIgnoreCase(type)) {
                meetingType = MeetingRoom.MeetingType.PAIR;
            } else if ("mixed".equalsIgnoreCase(type)) {
                meetingType = MeetingRoom.MeetingType.MIXED;
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "잘못된 미팅 타입입니다. (pair 또는 mixed)"
                ));
            }
            
            List<MeetingRoom> meetingRooms = meetingRoomService.getMeetingRoomsByType(meetingType);
            
            List<Map<String, Object>> result = meetingRooms.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return ResponseEntity.ok(Map.of(
                "message", type + " 타입 미팅방 목록",
                "meetingRooms", result
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "미팅방 목록 조회 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    private Map<String, Object> convertToResponse(MeetingRoom meetingRoom) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", meetingRoom.getId());
        response.put("title", meetingRoom.getTitle());
        response.put("description", meetingRoom.getDescription());
        response.put("type", meetingRoom.getType());
        response.put("participants", meetingRoom.getParticipants());
        response.put("creatorId", meetingRoom.getCreatorId());
        response.put("active", meetingRoom.isActive());
        response.put("maxParticipants", meetingRoom.getMaxParticipants());
        response.put("currentParticipants", meetingRoom.getParticipants().size());
        response.put("createdAt", meetingRoom.getCreatedAt());
        response.put("updatedAt", meetingRoom.getUpdatedAt());
        response.put("scheduledAt", meetingRoom.getScheduledAt());
        return response;
    }
    
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user.getId();
        }
        throw new RuntimeException("User not authenticated");
    }
}

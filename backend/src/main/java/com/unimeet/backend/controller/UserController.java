package com.unimeet.backend.controller;

import com.unimeet.backend.domain.User;
import com.unimeet.backend.dto.UpdateProfileRequest;
import com.unimeet.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:19006"})
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile() {
        try {
            System.out.println("=== getProfile 시작 ===");
            String userId = getCurrentUserId();
            System.out.println("getCurrentUserId 성공: " + userId);
            
            User user = userService.getUserById(userId);
            System.out.println("getUserById 성공: " + user.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("nickname", user.getNickname());
            response.put("phone", user.getPhone());
            response.put("role", user.getRole());
            response.put("mbti", user.getMbti());
            response.put("interests", user.getInterests());
            response.put("personalityKeywords", user.getPersonalityKeywords());
            response.put("idealType", user.getIdealType());
            
            System.out.println("응답 생성 완료");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("getProfile 에러: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            String userId = getCurrentUserId();
            User updatedUser = userService.updateProfile(userId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedUser.getId());
            response.put("email", updatedUser.getEmail());
            response.put("name", updatedUser.getName());
            response.put("nickname", updatedUser.getNickname());
            response.put("phone", updatedUser.getPhone());
            response.put("mbti", updatedUser.getMbti());
            response.put("interests", updatedUser.getInterests());
            response.put("personalityKeywords", updatedUser.getPersonalityKeywords());
            response.put("idealType", updatedUser.getIdealType());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable String userId) {
        try {
            User user = userService.getUserById(userId);
            
            // 공개 정보만 반환 (비밀번호, 이메일 등 제외)
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("nickname", user.getNickname());
            response.put("mbti", user.getMbti());
            response.put("interests", user.getInterests());
            response.put("personalityKeywords", user.getPersonalityKeywords());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
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
        // 디버깅을 위한 로그 추가
        if (authentication != null) {
            System.out.println("Principal type: " + authentication.getPrincipal().getClass().getName());
            System.out.println("Principal: " + authentication.getPrincipal());
        } else {
            System.out.println("Authentication is null");
        }
        throw new RuntimeException("User not authenticated");
    }
}


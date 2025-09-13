package com.unimeet.backend.controller;

import com.unimeet.backend.domain.Match;
import com.unimeet.backend.domain.User;
import com.unimeet.backend.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:19006"})
public class MatchController {

    private final MatchingService matchingService;
    private final com.unimeet.backend.service.CompatibilityService compatibilityService;
    private final com.unimeet.backend.service.UserService userService;

    /**
     * 매칭 후보 조회
     */
    @GetMapping("/candidates")
    public ResponseEntity<List<Match>> getMatchCandidates(@RequestParam(defaultValue = "10") int limit) {
        String userId = getCurrentUserId();
        List<Match> matches = matchingService.findMatches(userId, limit);
        return ResponseEntity.ok(matches);
    }

    /**
     * 매칭 요청 생성
     */
    @PostMapping("/request")
    public ResponseEntity<?> createMatchRequest(@RequestBody Map<String, String> request) {
        try {
            String currentUserId = getCurrentUserId();
            String targetUserId = request.get("targetUserId");
            
            if (targetUserId == null || targetUserId.isEmpty()) {
                return ResponseEntity.badRequest().body("Target user ID is required");
            }
            
            Match match = matchingService.createMatch(currentUserId, targetUserId);
            return ResponseEntity.ok(match);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 매칭 수락
     */
    @PostMapping("/{matchId}/accept")
    public ResponseEntity<?> acceptMatch(@PathVariable String matchId) {
        try {
            String userId = getCurrentUserId();
            Match match = matchingService.acceptMatch(matchId, userId);
            return ResponseEntity.ok(match);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 매칭 거절
     */
    @PostMapping("/{matchId}/reject")
    public ResponseEntity<?> rejectMatch(@PathVariable String matchId) {
        try {
            String userId = getCurrentUserId();
            Match match = matchingService.rejectMatch(matchId, userId);
            return ResponseEntity.ok(match);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 내 매칭 목록 조회 (페이지네이션)
     */
    @GetMapping("/my-matches")
    public ResponseEntity<com.unimeet.backend.dto.PageResponse<Match>> getMyMatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "matchedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        String userId = getCurrentUserId();
        
        com.unimeet.backend.dto.PageRequest pageRequest = new com.unimeet.backend.dto.PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setSortDirection(sortDirection);
        
        org.springframework.data.domain.Page<Match> matchPage = matchingService.getUserMatches(userId, pageRequest.toPageable());
        com.unimeet.backend.dto.PageResponse<Match> response = com.unimeet.backend.dto.PageResponse.of(matchPage);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 수락된 매칭 목록 조회
     */
    @GetMapping("/accepted")
    public ResponseEntity<List<Match>> getAcceptedMatches() {
        String userId = getCurrentUserId();
        List<Match> matches = matchingService.getAcceptedMatches(userId);
        return ResponseEntity.ok(matches);
    }

    /**
     * 두 사용자 간 호환성 점수 상세 조회 (테스트용)
     */
    @GetMapping("/compatibility/{targetUserId}")
    public ResponseEntity<?> getCompatibilityScore(@PathVariable String targetUserId) {
        try {
            String currentUserId = getCurrentUserId();
            User currentUser = userService.getUserById(currentUserId);
            User targetUser = userService.getUserById(targetUserId);
            
            Map<String, Double> detailedScore = compatibilityService.getDetailedCompatibilityScore(currentUser, targetUser);
            
            Map<String, Object> response = Map.of(
                "currentUser", Map.of("id", currentUser.getId(), "name", currentUser.getName(), "department", currentUser.getDepartment()),
                "targetUser", Map.of("id", targetUser.getId(), "name", targetUser.getName(), "department", targetUser.getDepartment()),
                "compatibilityScores", detailedScore
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error calculating compatibility: " + e.getMessage());
        }
    }

    /**
     * 매칭 알고리즘 테스트 (개발용)
     */
    @GetMapping("/test-algorithm")
    public ResponseEntity<?> testMatchingAlgorithm(@RequestParam(defaultValue = "5") int limit) {
        try {
            String userId = getCurrentUserId();
            List<Match> matches = matchingService.findMatches(userId, limit);
            
            // 상세 정보 포함한 응답 생성
            List<Map<String, Object>> detailedMatches = matches.stream()
                .map(match -> {
                    try {
                        User currentUser = userService.getUserById(match.getUserAId());
                        User targetUser = userService.getUserById(match.getUserBId());
                        Map<String, Double> scores = compatibilityService.getDetailedCompatibilityScore(currentUser, targetUser);
                        
                        Map<String, Object> result = new HashMap<>();
                        result.put("match", match);
                        
                        Map<String, Object> targetUserInfo = new HashMap<>();
                        targetUserInfo.put("id", targetUser.getId());
                        targetUserInfo.put("name", targetUser.getName());
                        targetUserInfo.put("department", targetUser.getDepartment());
                        targetUserInfo.put("mbti", targetUser.getMbti() != null ? targetUser.getMbti() : "N/A");
                        targetUserInfo.put("interests", targetUser.getInterests() != null ? targetUser.getInterests() : List.of());
                        targetUserInfo.put("height", targetUser.getHeight() != null ? targetUser.getHeight() : "N/A");
                        result.put("targetUser", targetUserInfo);
                        
                        result.put("compatibilityBreakdown", scores);
                        return result;
                    } catch (Exception e) {
                        Map<String, Object> errorResult = new HashMap<>();
                        errorResult.put("error", "Failed to load user data: " + e.getMessage());
                        return errorResult;
                    }
                })
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "message", "매칭 알고리즘 테스트 결과",
                "totalMatches", matches.size(),
                "matches", detailedMatches
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error testing matching algorithm: " + e.getMessage());
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
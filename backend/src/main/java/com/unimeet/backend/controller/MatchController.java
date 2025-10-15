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
     * AI 매칭 수행 (시연용)
     */
    @PostMapping("/find")
    public ResponseEntity<?> findMatches(@RequestParam(defaultValue = "10") int limit) {
        try {
            String userId = getCurrentUserId();
            User currentUser = userService.getUserById(userId);
            
            // 모든 다른 사용자들을 후보로 가져오기
            List<User> allUsers = userService.getAllUsers();
            List<User> candidates = allUsers.stream()
                .filter(user -> !user.getId().equals(userId))
                .filter(user -> user.isVerified())
                .collect(java.util.stream.Collectors.toList());
            
            if (candidates.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "매칭 가능한 후보가 없습니다",
                    "matches", List.of()
                ));
            }
            
            // 호환성 점수 계산 및 정렬
            List<Map<String, Object>> matchResults = candidates.stream()
                .map(candidate -> {
                    double compatibilityScore = compatibilityService.calculateCompatibility(currentUser, candidate);
                    Map<String, Double> detailedScores = compatibilityService.getDetailedCompatibilityScore(currentUser, candidate);
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("userId", candidate.getId());
                    result.put("name", candidate.getName());
                    result.put("nickname", candidate.getNickname());
                    result.put("department", candidate.getDepartment());
                    result.put("studentId", candidate.getStudentId());
                    result.put("gender", candidate.getGender() != null ? candidate.getGender() : "N/A");
                    result.put("age", calculateAge(candidate.getBirth()));
                    result.put("mbti", candidate.getMbti() != null ? candidate.getMbti() : "N/A");
                    result.put("interests", candidate.getInterests() != null ? candidate.getInterests() : List.of());
                    result.put("height", candidate.getHeight() != null ? candidate.getHeight() : "N/A");
                    result.put("prefer", candidate.getPrefer());
                    result.put("nonPrefer", candidate.getNonPrefer());
                    result.put("compatibilityScore", compatibilityScore);
                    result.put("detailedScores", detailedScores);
                    
                    // 공통 관심사 계산
                    List<String> currentInterests = currentUser.getInterests() != null ? currentUser.getInterests() : List.of();
                    List<String> candidateInterests = candidate.getInterests() != null ? candidate.getInterests() : List.of();
                    List<String> commonInterests = currentInterests.stream()
                        .filter(candidateInterests::contains)
                        .collect(java.util.stream.Collectors.toList());
                    result.put("commonInterests", commonInterests);
                    
                    return result;
                })
                .sorted((a, b) -> Double.compare((Double) b.get("compatibilityScore"), (Double) a.get("compatibilityScore")))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "message", "AI 매칭 완료",
                "requestUser", Map.of(
                    "name", currentUser.getName(),
                    "mbti", currentUser.getMbti() != null ? currentUser.getMbti() : "N/A",
                    "department", currentUser.getDepartment(),
                    "interests", currentUser.getInterests() != null ? currentUser.getInterests() : List.of()
                ),
                "totalCandidates", candidates.size(),
                "matches", matchResults
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "매칭 처리 중 오류 발생",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 매칭 요청 보내기
     */
    @PostMapping("/request")
    public ResponseEntity<?> sendMatchRequest(@RequestBody Map<String, Object> request) {
        try {
            String requesterId = getCurrentUserId();
            String targetId = (String) request.get("targetId");
            String message = (String) request.get("message");
            
            if (targetId == null || targetId.equals(requesterId)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "잘못된 매칭 대상입니다"
                ));
            }
            
            Match match = matchingService.sendMatchRequest(requesterId, targetId, message);
            
            return ResponseEntity.ok(Map.of(
                "message", "매칭 요청을 보냈습니다",
                "matchId", match.getId(),
                "targetId", targetId
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "매칭 요청 실패",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 매칭 요청 수락
     */
    @PostMapping("/{matchId}/accept")
    public ResponseEntity<?> acceptMatch(@PathVariable String matchId) {
        try {
            String userId = getCurrentUserId();
            Match match = matchingService.acceptMatchRequest(matchId, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "매칭을 수락했습니다",
                "matchId", matchId,
                "status", match.getStatus()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "매칭 수락 실패",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 매칭 요청 거절
     */
    @PostMapping("/{matchId}/reject")
    public ResponseEntity<?> rejectMatch(@PathVariable String matchId) {
        try {
            String userId = getCurrentUserId();
            Match match = matchingService.rejectMatchRequest(matchId, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "매칭을 거절했습니다",
                "matchId", matchId,
                "status", match.getStatus()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "매칭 거절 실패",
                "message", e.getMessage()
            ));
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
     * 받은 매칭 요청 목록
     */
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedMatches() {
        try {
            String userId = getCurrentUserId();
            List<Match> matches = matchingService.getReceivedMatches(userId);
            
            List<Map<String, Object>> result = matches.stream()
                .map(match -> {
                    User requester = userService.getUserById(match.getRequesterId());
                    Map<String, Object> matchInfo = new HashMap<>();
                    matchInfo.put("matchId", match.getId());
                    matchInfo.put("requester", Map.of(
                        "id", requester.getId(),
                        "name", requester.getName(),
                        "nickname", requester.getNickname(),
                        "department", requester.getDepartment(),
                        "mbti", requester.getMbti() != null ? requester.getMbti() : "N/A"
                    ));
                    matchInfo.put("message", match.getMessage());
                    matchInfo.put("compatibilityScore", match.getCompatibilityScore());
                    matchInfo.put("status", match.getStatus());
                    matchInfo.put("createdAt", match.getCreatedAt());
                    return matchInfo;
                })
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "message", "받은 매칭 요청 목록",
                "matches", result
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "매칭 목록 조회 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 보낸 매칭 요청 목록
     */
    @GetMapping("/sent")
    public ResponseEntity<?> getSentMatches() {
        try {
            String userId = getCurrentUserId();
            List<Match> matches = matchingService.getSentMatches(userId);
            
            List<Map<String, Object>> result = matches.stream()
                .map(match -> {
                    User target = userService.getUserById(match.getTargetId());
                    Map<String, Object> matchInfo = new HashMap<>();
                    matchInfo.put("matchId", match.getId());
                    matchInfo.put("target", Map.of(
                        "id", target.getId(),
                        "name", target.getName(),
                        "nickname", target.getNickname(),
                        "department", target.getDepartment(),
                        "mbti", target.getMbti() != null ? target.getMbti() : "N/A"
                    ));
                    matchInfo.put("message", match.getMessage());
                    matchInfo.put("compatibilityScore", match.getCompatibilityScore());
                    matchInfo.put("status", match.getStatus());
                    matchInfo.put("createdAt", match.getCreatedAt());
                    matchInfo.put("respondedAt", match.getRespondedAt());
                    return matchInfo;
                })
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "message", "보낸 매칭 요청 목록",
                "matches", result
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "매칭 목록 조회 실패",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 수락된 매칭 목록 (매칭된 사람들)
     */
    @GetMapping("/accepted")
    public ResponseEntity<?> getAcceptedMatches() {
        try {
            String userId = getCurrentUserId();
            List<Match> matches = matchingService.getAcceptedMatchesList(userId);
            
            List<Map<String, Object>> result = matches.stream()
                .map(match -> {
                    // 상대방 정보 가져오기
                    String partnerId = match.getRequesterId().equals(userId) ? 
                                     match.getTargetId() : match.getRequesterId();
                    User partner = userService.getUserById(partnerId);
                    
                    Map<String, Object> matchInfo = new HashMap<>();
                    matchInfo.put("matchId", match.getId());
                    matchInfo.put("partner", Map.of(
                        "id", partner.getId(),
                        "name", partner.getName(),
                        "nickname", partner.getNickname(),
                        "department", partner.getDepartment(),
                        "mbti", partner.getMbti() != null ? partner.getMbti() : "N/A",
                        "interests", partner.getInterests() != null ? partner.getInterests() : List.of()
                    ));
                    matchInfo.put("compatibilityScore", match.getCompatibilityScore());
                    matchInfo.put("matchedAt", match.getRespondedAt());
                    return matchInfo;
                })
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "message", "매칭된 사람들",
                "matches", result
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "매칭 목록 조회 실패",
                "message", e.getMessage()
            ));
        }
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
    
    /**
     * 생년월일로부터 나이 계산
     */
    private int calculateAge(String birth) {
        if (birth == null || birth.isEmpty()) {
            return 0;
        }
        try {
            String[] parts = birth.split("-");
            if (parts.length >= 1) {
                int birthYear = Integer.parseInt(parts[0]);
                int currentYear = java.time.Year.now().getValue();
                return currentYear - birthYear + 1; // 한국식 나이
            }
        } catch (Exception e) {
            // 파싱 실패 시 0 반환
        }
        return 0;
    }
}
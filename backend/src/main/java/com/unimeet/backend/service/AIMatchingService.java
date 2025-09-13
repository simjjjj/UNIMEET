package com.unimeet.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimeet.backend.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIMatchingService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${ai.service.url:http://localhost:8001}")
    private String aiServiceUrl;
    
    @Value("${ai.service.enabled:false}")
    private boolean aiServiceEnabled;

    /**
     * AI 서비스를 통한 매칭 수행
     */
    public List<MatchResult> findMatches(User targetUser, List<User> candidates, int topK) {
        if (!aiServiceEnabled) {
            log.info("AI 서비스가 비활성화되어 있습니다. 기본 매칭을 사용합니다.");
            return Collections.emptyList();
        }

        try {
            // 요청 데이터 구성
            Map<String, Object> request = new HashMap<>();
            request.put("target_user", convertUserToProfile(targetUser));
            request.put("candidate_users", candidates.stream()
                    .map(this::convertUserToProfile)
                    .collect(Collectors.toList()));
            request.put("top_k", topK);

            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            // AI 서비스 호출
            String url = aiServiceUrl + "/match";
            ResponseEntity<MatchResult[]> response = restTemplate.postForEntity(
                    url, entity, MatchResult[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<MatchResult> results = Arrays.asList(response.getBody());
                log.info("AI 매칭 완료: {} 명의 후보 중 {} 명 반환", candidates.size(), results.size());
                return results;
            } else {
                log.warn("AI 서비스 응답 오류: {}", response.getStatusCode());
                return Collections.emptyList();
            }

        } catch (Exception e) {
            log.error("AI 매칭 서비스 호출 실패: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 두 사용자 간 호환성 계산
     */
    public CompatibilityResult calculateCompatibility(User user1, User user2) {
        if (!aiServiceEnabled) {
            log.info("AI 서비스가 비활성화되어 있습니다.");
            return null;
        }

        try {
            // 요청 데이터 구성
            Map<String, Object> request = new HashMap<>();
            request.put("user1", convertUserToProfile(user1));
            request.put("user2", convertUserToProfile(user2));

            // HTTP 요청
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            String url = aiServiceUrl + "/compatibility";
            ResponseEntity<CompatibilityResult> response = restTemplate.postForEntity(
                    url, entity, CompatibilityResult.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.debug("AI 호환성 계산 완료: {} vs {}", user1.getId(), user2.getId());
                return response.getBody();
            } else {
                log.warn("AI 호환성 계산 실패: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            log.error("AI 호환성 계산 서비스 호출 실패: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * AI 서비스 상태 확인
     */
    public boolean isAIServiceAvailable() {
        if (!aiServiceEnabled) {
            return false;
        }

        try {
            String url = aiServiceUrl + "/";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.warn("AI 서비스 연결 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * User 엔티티를 AI 서비스용 프로필로 변환
     */
    private Map<String, Object> convertUserToProfile(User user) {
        Map<String, Object> profile = new HashMap<>();
        
        profile.put("user_id", user.getId());
        profile.put("mbti", user.getMbti());
        profile.put("interests", user.getInterests() != null ? user.getInterests() : Collections.emptyList());
        profile.put("personality_keywords", user.getPersonalityKeywords() != null ? 
                user.getPersonalityKeywords() : Collections.emptyList());
        profile.put("department", user.getDepartment());
        
        // 생년월일에서 연도 추출
        if (user.getBirth() != null) {
            try {
                String[] parts = user.getBirth().split("-");
                if (parts.length >= 1) {
                    profile.put("birth_year", Integer.parseInt(parts[0]));
                }
            } catch (Exception e) {
                log.warn("생년월일 파싱 실패: {}", user.getBirth());
            }
        }
        
        // 키 정보 변환
        if (user.getHeight() != null) {
            try {
                String heightStr = user.getHeight().replaceAll("[^0-9]", "");
                if (!heightStr.isEmpty()) {
                    profile.put("height", Integer.parseInt(heightStr));
                }
            } catch (Exception e) {
                log.warn("키 정보 파싱 실패: {}", user.getHeight());
            }
        }
        
        // 성별 정보 (User 엔티티에 성별 필드가 있다면)
        // profile.put("gender", user.getGender());
        
        return profile;
    }

    /**
     * 매칭 결과 데이터 클래스
     */
    public static class MatchResult {
        private String userId;
        private double compatibilityScore;
        private Map<String, Double> detailedScores;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public double getCompatibilityScore() { return compatibilityScore; }
        public void setCompatibilityScore(double compatibilityScore) { this.compatibilityScore = compatibilityScore; }
        
        public Map<String, Double> getDetailedScores() { return detailedScores; }
        public void setDetailedScores(Map<String, Double> detailedScores) { this.detailedScores = detailedScores; }
    }

    /**
     * 호환성 결과 데이터 클래스
     */
    public static class CompatibilityResult {
        private String user1Id;
        private String user2Id;
        private double compatibilityScore;
        private Map<String, Double> detailedScores;

        // Getters and Setters
        public String getUser1Id() { return user1Id; }
        public void setUser1Id(String user1Id) { this.user1Id = user1Id; }
        
        public String getUser2Id() { return user2Id; }
        public void setUser2Id(String user2Id) { this.user2Id = user2Id; }
        
        public double getCompatibilityScore() { return compatibilityScore; }
        public void setCompatibilityScore(double compatibilityScore) { this.compatibilityScore = compatibilityScore; }
        
        public Map<String, Double> getDetailedScores() { return detailedScores; }
        public void setDetailedScores(Map<String, Double> detailedScores) { this.detailedScores = detailedScores; }
    }
}
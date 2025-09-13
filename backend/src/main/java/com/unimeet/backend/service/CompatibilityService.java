package com.unimeet.backend.service;

import com.unimeet.backend.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CompatibilityService {

    // MBTI 호환성 매트릭스
    private static final Map<String, Set<String>> MBTI_COMPATIBILITY;
    
    static {
        Map<String, Set<String>> compatibility = new HashMap<>();
        compatibility.put("INTJ", Set.of("ENFP", "ENTP", "INFJ", "INFP"));
        compatibility.put("INTP", Set.of("ENFJ", "ENTJ", "INFJ", "INFP"));
        compatibility.put("ENTJ", Set.of("INFP", "INTP", "ENFP", "ENTP"));
        compatibility.put("ENTP", Set.of("INFJ", "INTJ", "ENFJ", "ENTJ"));
        compatibility.put("INFJ", Set.of("ENFP", "ENTP", "INFP", "ENFJ"));
        compatibility.put("INFP", Set.of("ENFJ", "ENTJ", "INFJ", "ENFP"));
        compatibility.put("ENFJ", Set.of("INFP", "INTP", "INFJ", "ENFP"));
        compatibility.put("ENFP", Set.of("INTJ", "INFJ", "ENFJ", "ENTP"));
        compatibility.put("ISTJ", Set.of("ESFP", "ESTP", "ISFJ", "ISFP"));
        compatibility.put("ISFJ", Set.of("ESFP", "ESTP", "ISTJ", "ISFP"));
        compatibility.put("ESTJ", Set.of("ISFP", "ISTP", "ESFP", "ESTP"));
        compatibility.put("ESFJ", Set.of("ISFP", "ISTP", "ISFJ", "ESFP"));
        compatibility.put("ISTP", Set.of("ESFJ", "ESTJ", "ISFJ", "ESFP"));
        compatibility.put("ISFP", Set.of("ESFJ", "ESTJ", "ISFJ", "ESFP"));
        compatibility.put("ESTP", Set.of("ISFJ", "ISTJ", "ESFJ", "ESFP"));
        compatibility.put("ESFP", Set.of("ISFJ", "ISTJ", "ESFJ", "ESTP"));
        MBTI_COMPATIBILITY = Collections.unmodifiableMap(compatibility);
    }

    /**
     * 두 사용자 간의 전체 호환성 점수 계산 (새로운 필드 포함)
     */
    public double calculateCompatibility(User userA, User userB) {
        double mbtiScore = calculateMbtiCompatibility(userA.getMbti(), userB.getMbti());
        double interestScore = calculateInterestCompatibility(userA.getInterests(), userB.getInterests());
        double personalityScore = calculatePersonalityCompatibility(
            userA.getPersonalityKeywords(), userB.getPersonalityKeywords());
        double idealTypeScore = calculateIdealTypeCompatibility(userA, userB);
        
        // 새로운 필드들 추가
        double departmentScore = calculateDepartmentCompatibility(userA.getDepartment(), userB.getDepartment());
        double ageScore = calculateAgeCompatibility(userA.getBirth(), userB.getBirth());
        double heightScore = calculateHeightCompatibility(userA, userB);

        // 가중치 적용 (총 100%)
        double totalScore = (mbtiScore * 0.25) +        // MBTI 호환성 25%
                           (interestScore * 0.20) +     // 관심사 유사도 20%
                           (personalityScore * 0.20) +  // 성격 키워드 20%
                           (idealTypeScore * 0.15) +    // 이상형 매칭 15%
                           (departmentScore * 0.10) +   // 학과 호환성 10%
                           (ageScore * 0.05) +          // 나이 호환성 5%
                           (heightScore * 0.05);        // 키 호환성 5%

        log.debug("Compatibility calculated for users {} and {}: MBTI={}, Interest={}, Personality={}, IdealType={}, Department={}, Age={}, Height={}, Total={}", 
                 userA.getId(), userB.getId(), mbtiScore, interestScore, personalityScore, idealTypeScore, 
                 departmentScore, ageScore, heightScore, totalScore);

        return Math.round(totalScore * 100.0) / 100.0; // 소수점 2자리까지
    }

    /**
     * MBTI 호환성 계산
     */
    private double calculateMbtiCompatibility(String mbtiA, String mbtiB) {
        if (mbtiA == null || mbtiB == null) {
            return 0.5; // 기본값
        }

        // 완전 일치
        if (mbtiA.equals(mbtiB)) {
            return 0.8;
        }

        // 호환성 매트릭스 확인
        Set<String> compatibleTypes = MBTI_COMPATIBILITY.get(mbtiA);
        if (compatibleTypes != null && compatibleTypes.contains(mbtiB)) {
            return 0.9;
        }

        // 부분 호환성 계산 (공통 글자 수)
        int commonChars = 0;
        for (int i = 0; i < Math.min(mbtiA.length(), mbtiB.length()); i++) {
            if (mbtiA.charAt(i) == mbtiB.charAt(i)) {
                commonChars++;
            }
        }

        return 0.3 + (commonChars * 0.1); // 0.3 ~ 0.7
    }

    /**
     * 관심사 호환성 계산
     */
    private double calculateInterestCompatibility(List<String> interestsA, List<String> interestsB) {
        if (interestsA == null || interestsB == null || 
            interestsA.isEmpty() || interestsB.isEmpty()) {
            return 0.5;
        }

        Set<String> setA = new HashSet<>(interestsA);
        Set<String> setB = new HashSet<>(interestsB);
        
        // 교집합 계산
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        
        // 합집합 계산
        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);
        
        if (union.isEmpty()) {
            return 0.5;
        }

        // 자카드 유사도 계산
        double jaccardSimilarity = (double) intersection.size() / union.size();
        
        // 0.3 ~ 1.0 범위로 조정
        return 0.3 + (jaccardSimilarity * 0.7);
    }

    /**
     * 성격 키워드 호환성 계산
     */
    private double calculatePersonalityCompatibility(List<String> personalityA, List<String> personalityB) {
        if (personalityA == null || personalityB == null || 
            personalityA.isEmpty() || personalityB.isEmpty()) {
            return 0.5;
        }

        Set<String> setA = new HashSet<>(personalityA);
        Set<String> setB = new HashSet<>(personalityB);
        
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        
        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);
        
        if (union.isEmpty()) {
            return 0.5;
        }

        double jaccardSimilarity = (double) intersection.size() / union.size();
        return 0.4 + (jaccardSimilarity * 0.6);
    }

    /**
     * 이상형 호환성 계산
     */
    private double calculateIdealTypeCompatibility(User userA, User userB) {
        double scoreA = calculateIdealTypeMatch(userA.getIdealType(), userB);
        double scoreB = calculateIdealTypeMatch(userB.getIdealType(), userA);
        
        // 양방향 평균
        return (scoreA + scoreB) / 2.0;
    }

    /**
     * 한 방향 이상형 매칭 계산
     */
    private double calculateIdealTypeMatch(User.IdealType idealType, User targetUser) {
        if (idealType == null) {
            return 0.7; // 이상형이 없으면 중간 점수
        }

        double score = 0.0;
        int criteria = 0;

        // MBTI 매칭
        if (idealType.getMbti() != null && targetUser.getMbti() != null) {
            criteria++;
            if (idealType.getMbti().equals(targetUser.getMbti())) {
                score += 1.0;
            } else {
                Set<String> compatibleTypes = MBTI_COMPATIBILITY.get(idealType.getMbti());
                if (compatibleTypes != null && compatibleTypes.contains(targetUser.getMbti())) {
                    score += 0.8;
                } else {
                    score += 0.3;
                }
            }
        }

        // 성격 키워드 매칭
        if (idealType.getPersonalityKeywords() != null && 
            targetUser.getPersonalityKeywords() != null &&
            !idealType.getPersonalityKeywords().isEmpty() &&
            !targetUser.getPersonalityKeywords().isEmpty()) {
            criteria++;
            
            Set<String> idealKeywords = new HashSet<>(idealType.getPersonalityKeywords());
            Set<String> userKeywords = new HashSet<>(targetUser.getPersonalityKeywords());
            
            Set<String> intersection = new HashSet<>(idealKeywords);
            intersection.retainAll(userKeywords);
            
            double keywordMatch = (double) intersection.size() / idealKeywords.size();
            score += keywordMatch;
        }

        return criteria > 0 ? score / criteria : 0.7;
    }

    /**
     * 학과 호환성 계산
     */
    private double calculateDepartmentCompatibility(String departmentA, String departmentB) {
        if (departmentA == null || departmentB == null) {
            return 0.5; // 기본값
        }

        // 완전 일치 (같은 학과)
        if (departmentA.equals(departmentB)) {
            return 1.0;
        }

        // 관련 학과 그룹 정의
        Map<String, Set<String>> relatedDepartments = Map.of(
            "공학계열", Set.of("컴퓨터공학과", "전자공학과", "기계공학과", "화학공학과", "건축공학과", "산업공학과"),
            "인문계열", Set.of("국어국문학과", "영어영문학과", "사학과", "철학과", "문예창작학과"),
            "사회계열", Set.of("경영학과", "경제학과", "정치외교학과", "사회학과", "심리학과", "행정학과"),
            "자연계열", Set.of("수학과", "물리학과", "화학과", "생물학과", "지구환경과학과"),
            "예체능계열", Set.of("미술학과", "음악학과", "체육학과", "디자인학과", "무용학과"),
            "의료계열", Set.of("의학과", "간호학과", "약학과", "치의학과", "한의학과")
        );

        // 같은 계열인지 확인
        for (Set<String> group : relatedDepartments.values()) {
            if (group.contains(departmentA) && group.contains(departmentB)) {
                return 0.8; // 같은 계열
            }
        }

        // 다른 계열
        return 0.4;
    }

    /**
     * 나이 호환성 계산 (생년월일 기반)
     */
    private double calculateAgeCompatibility(String birthA, String birthB) {
        if (birthA == null || birthB == null) {
            return 0.5; // 기본값
        }

        try {
            // YYYY-MM-DD 형식에서 연도 추출
            int yearA = Integer.parseInt(birthA.substring(0, 4));
            int yearB = Integer.parseInt(birthB.substring(0, 4));
            
            int ageDiff = Math.abs(yearA - yearB);
            
            // 나이 차이에 따른 호환성
            if (ageDiff == 0) return 1.0;      // 동갑
            if (ageDiff == 1) return 0.9;      // 1살 차이
            if (ageDiff == 2) return 0.8;      // 2살 차이
            if (ageDiff <= 3) return 0.7;      // 3살 이하
            if (ageDiff <= 5) return 0.6;      // 5살 이하
            if (ageDiff <= 7) return 0.4;      // 7살 이하
            
            return 0.2; // 7살 초과
            
        } catch (Exception e) {
            log.warn("Failed to parse birth dates: {} and {}", birthA, birthB);
            return 0.5;
        }
    }

    /**
     * 키 호환성 계산
     */
    private double calculateHeightCompatibility(User userA, User userB) {
        String heightA = userA.getHeight();
        String heightB = userB.getHeight();
        
        if (heightA == null || heightB == null) {
            return 0.5; // 기본값
        }

        try {
            // "175" -> 175 변환
            int heightAInt = Integer.parseInt(heightA.replaceAll("[^0-9]", ""));
            int heightBInt = Integer.parseInt(heightB.replaceAll("[^0-9]", ""));
            
            int heightDiff = Math.abs(heightAInt - heightBInt);
            
            // 키 차이에 따른 호환성
            if (heightDiff <= 3) return 1.0;       // 3cm 이하
            if (heightDiff <= 5) return 0.9;       // 5cm 이하
            if (heightDiff <= 10) return 0.8;      // 10cm 이하
            if (heightDiff <= 15) return 0.6;      // 15cm 이하
            if (heightDiff <= 20) return 0.4;      // 20cm 이하
            
            return 0.2; // 20cm 초과
            
        } catch (Exception e) {
            log.warn("Failed to parse heights: {} and {}", heightA, heightB);
            return 0.5;
        }
    }

    /**
     * 매칭 점수 상세 정보 반환 (디버깅용)
     */
    public Map<String, Double> getDetailedCompatibilityScore(User userA, User userB) {
        Map<String, Double> scores = new HashMap<>();
        
        scores.put("mbti", calculateMbtiCompatibility(userA.getMbti(), userB.getMbti()));
        scores.put("interests", calculateInterestCompatibility(userA.getInterests(), userB.getInterests()));
        scores.put("personality", calculatePersonalityCompatibility(userA.getPersonalityKeywords(), userB.getPersonalityKeywords()));
        scores.put("idealType", calculateIdealTypeCompatibility(userA, userB));
        scores.put("department", calculateDepartmentCompatibility(userA.getDepartment(), userB.getDepartment()));
        scores.put("age", calculateAgeCompatibility(userA.getBirth(), userB.getBirth()));
        scores.put("height", calculateHeightCompatibility(userA, userB));
        scores.put("total", calculateCompatibility(userA, userB));
        
        return scores;
    }
}
package com.unimeet.backend.service;

import com.unimeet.backend.domain.Match;
import com.unimeet.backend.domain.User;
import com.unimeet.backend.repository.MatchRepository;
import com.unimeet.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final CompatibilityService compatibilityService;
    private final AIMatchingService aiMatchingService;

    /**
     * 사용자에게 매칭 후보들을 찾아서 반환 (페이지네이션)
     */
    public List<Match> findMatches(String userId, int limit) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 이미 매칭된 사용자들 제외
        List<String> excludedUserIds = matchRepository.findByUserAIdOrUserBId(userId, userId)
                .stream()
                .map(match -> match.getUserAId().equals(userId) ? match.getUserBId() : match.getUserAId())
                .collect(Collectors.toList());
        
        excludedUserIds.add(userId); // 자기 자신 제외

        // 매칭 가능한 모든 사용자 조회
        List<User> potentialMatches = userRepository.findAll()
                .stream()
                .filter(user -> !excludedUserIds.contains(user.getId()))
                .collect(Collectors.toList());

        // AI 매칭 시도 (AI 서비스가 활성화된 경우)
        if (aiMatchingService.isAIServiceAvailable()) {
            log.info("AI 매칭 서비스 사용: 사용자 {}", userId);
            return findMatchesWithAI(currentUser, potentialMatches, limit);
        }
        
        // 기본 규칙 기반 매칭
        log.info("규칙 기반 매칭 사용: 사용자 {}", userId);
        return potentialMatches.stream()
                .map(candidate -> {
                    double score = compatibilityService.calculateCompatibility(currentUser, candidate);
                    return Match.builder()
                            .userAId(userId)
                            .userBId(candidate.getId())
                            .score(score)
                            .matchedAt(LocalDateTime.now())
                            .status(Match.MatchStatus.PENDING)
                            .build();
                })
                .filter(match -> match.getScore() >= 0.6) // 60% 이상 호환성
                .sorted((m1, m2) -> Double.compare(m2.getScore(), m1.getScore())) // 점수 높은 순
                .limit(limit > 0 ? limit : 10) // 제한된 수만큼
                .collect(Collectors.toList());
    }

    /**
     * 매칭 요청 생성
     */
    public Match createMatch(String userAId, String userBId) {
        // 이미 매칭이 존재하는지 확인
        Optional<Match> existingMatch = matchRepository.findByUserAIdAndUserBIdOrUserBIdAndUserAId(
                userAId, userBId, userAId, userBId);
        
        if (existingMatch.isPresent()) {
            throw new RuntimeException("Match already exists");
        }

        User userA = userRepository.findById(userAId)
                .orElseThrow(() -> new RuntimeException("User A not found"));
        User userB = userRepository.findById(userBId)
                .orElseThrow(() -> new RuntimeException("User B not found"));

        double score = compatibilityService.calculateCompatibility(userA, userB);

        Match match = Match.builder()
                .userAId(userAId)
                .userBId(userBId)
                .score(score)
                .matchedAt(LocalDateTime.now())
                .status(Match.MatchStatus.PENDING)
                .build();

        return matchRepository.save(match);
    }

    /**
     * AI 서비스를 사용한 매칭
     */
    private List<Match> findMatchesWithAI(User currentUser, List<User> potentialMatches, int limit) {
        try {
            // AI 서비스 호출
            List<AIMatchingService.MatchResult> aiResults = aiMatchingService.findMatches(
                    currentUser, potentialMatches, limit > 0 ? limit : 10);
            
            // AI 결과를 Match 객체로 변환
            return aiResults.stream()
                    .filter(result -> result.getCompatibilityScore() >= 0.6) // 60% 이상
                    .map(result -> Match.builder()
                            .userAId(currentUser.getId())
                            .userBId(result.getUserId())
                            .score(result.getCompatibilityScore())
                            .matchedAt(LocalDateTime.now())
                            .status(Match.MatchStatus.PENDING)
                            .build())
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("AI 매칭 실패, 기본 매칭으로 대체: {}", e.getMessage());
            
            // AI 실패 시 기본 매칭으로 대체
            return potentialMatches.stream()
                    .map(candidate -> {
                        double score = compatibilityService.calculateCompatibility(currentUser, candidate);
                        return Match.builder()
                                .userAId(currentUser.getId())
                                .userBId(candidate.getId())
                                .score(score)
                                .matchedAt(LocalDateTime.now())
                                .status(Match.MatchStatus.PENDING)
                                .build();
                    })
                    .filter(match -> match.getScore() >= 0.6)
                    .sorted((m1, m2) -> Double.compare(m2.getScore(), m1.getScore()))
                    .limit(limit > 0 ? limit : 10)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 매칭 수락
     */
    public Match acceptMatch(String matchId, String userId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // 매칭 대상자만 수락 가능
        if (!match.getUserBId().equals(userId)) {
            throw new RuntimeException("Unauthorized to accept this match");
        }

        if (match.getStatus() != Match.MatchStatus.PENDING) {
            throw new RuntimeException("Match is not in pending status");
        }

        match.setStatus(Match.MatchStatus.ACCEPTED);
        return matchRepository.save(match);
    }

    /**
     * 매칭 거절
     */
    public Match rejectMatch(String matchId, String userId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // 매칭 대상자만 거절 가능
        if (!match.getUserBId().equals(userId)) {
            throw new RuntimeException("Unauthorized to reject this match");
        }

        if (match.getStatus() != Match.MatchStatus.PENDING) {
            throw new RuntimeException("Match is not in pending status");
        }

        match.setStatus(Match.MatchStatus.REJECTED);
        return matchRepository.save(match);
    }

    /**
     * 사용자의 매칭 목록 조회 (페이지네이션)
     */
    public org.springframework.data.domain.Page<Match> getUserMatches(String userId, org.springframework.data.domain.Pageable pageable) {
        return matchRepository.findByUserAIdOrUserBId(userId, userId, pageable);
    }

    /**
     * 사용자의 매칭 목록 조회 (전체)
     */
    public List<Match> getUserMatches(String userId) {
        return matchRepository.findByUserAIdOrUserBId(userId, userId);
    }

    /**
     * 수락된 매칭 목록 조회
     */
    public List<Match> getAcceptedMatches(String userId) {
        return matchRepository.findByUserAIdOrUserBIdAndStatus(userId, userId, Match.MatchStatus.ACCEPTED);
    }
}
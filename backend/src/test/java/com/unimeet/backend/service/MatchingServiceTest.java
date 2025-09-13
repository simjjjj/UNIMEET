package com.unimeet.backend.service;

import com.unimeet.backend.domain.Match;
import com.unimeet.backend.domain.User;
import com.unimeet.backend.repository.MatchRepository;
import com.unimeet.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private CompatibilityService compatibilityService;

    @InjectMocks
    private MatchingService matchingService;

    private User userA;
    private User userB;
    private Match match;

    @BeforeEach
    void setUp() {
        userA = User.builder()
                .id("userA")
                .email("userA@test.com")
                .name("User A")
                .mbti("INTJ")
                .interests(List.of("독서", "영화감상"))
                .personalityKeywords(List.of("내향적", "논리적"))
                .build();

        userB = User.builder()
                .id("userB")
                .email("userB@test.com")
                .name("User B")
                .mbti("ENFP")
                .interests(List.of("독서", "여행"))
                .personalityKeywords(List.of("외향적", "창의적"))
                .build();

        match = Match.builder()
                .id("match123")
                .userAId("userA")
                .userBId("userB")
                .score(0.85)
                .matchedAt(LocalDateTime.now())
                .status(Match.MatchStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("매칭 후보 찾기 성공")
    void findMatches_Success() {
        // given
        given(userRepository.findById("userA")).willReturn(Optional.of(userA));
        given(matchRepository.findByUserAIdOrUserBId("userA", "userA")).willReturn(List.of());
        given(userRepository.findAll()).willReturn(List.of(userB));
        given(compatibilityService.calculateCompatibility(userA, userB)).willReturn(0.85);

        // when
        List<Match> result = matchingService.findMatches("userA", 10);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserAId()).isEqualTo("userA");
        assertThat(result.get(0).getUserBId()).isEqualTo("userB");
        assertThat(result.get(0).getScore()).isEqualTo(0.85);
        assertThat(result.get(0).getStatus()).isEqualTo(Match.MatchStatus.PENDING);
    }

    @Test
    @DisplayName("매칭 후보 찾기 - 낮은 호환성 필터링")
    void findMatches_LowCompatibilityFiltered() {
        // given
        given(userRepository.findById("userA")).willReturn(Optional.of(userA));
        given(matchRepository.findByUserAIdOrUserBId("userA", "userA")).willReturn(List.of());
        given(userRepository.findAll()).willReturn(List.of(userB));
        given(compatibilityService.calculateCompatibility(userA, userB)).willReturn(0.5); // 60% 미만

        // when
        List<Match> result = matchingService.findMatches("userA", 10);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("매칭 요청 생성 성공")
    void createMatch_Success() {
        // given
        given(matchRepository.findByUserAIdAndUserBIdOrUserBIdAndUserAId(
                "userA", "userB", "userA", "userB")).willReturn(Optional.empty());
        given(userRepository.findById("userA")).willReturn(Optional.of(userA));
        given(userRepository.findById("userB")).willReturn(Optional.of(userB));
        given(compatibilityService.calculateCompatibility(userA, userB)).willReturn(0.85);
        given(matchRepository.save(any(Match.class))).willReturn(match);

        // when
        Match result = matchingService.createMatch("userA", "userB");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserAId()).isEqualTo("userA");
        assertThat(result.getUserBId()).isEqualTo("userB");
        assertThat(result.getScore()).isEqualTo(0.85);
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    @DisplayName("매칭 요청 생성 실패 - 이미 존재하는 매칭")
    void createMatch_AlreadyExists() {
        // given
        given(matchRepository.findByUserAIdAndUserBIdOrUserBIdAndUserAId(
                "userA", "userB", "userA", "userB")).willReturn(Optional.of(match));

        // when & then
        assertThatThrownBy(() -> matchingService.createMatch("userA", "userB"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Match already exists");
    }

    @Test
    @DisplayName("매칭 수락 성공")
    void acceptMatch_Success() {
        // given
        given(matchRepository.findById("match123")).willReturn(Optional.of(match));
        
        Match acceptedMatch = Match.builder()
                .id("match123")
                .userAId("userA")
                .userBId("userB")
                .score(0.85)
                .matchedAt(LocalDateTime.now())
                .status(Match.MatchStatus.ACCEPTED)
                .build();
        
        given(matchRepository.save(any(Match.class))).willReturn(acceptedMatch);

        // when
        Match result = matchingService.acceptMatch("match123", "userB");

        // then
        assertThat(result.getStatus()).isEqualTo(Match.MatchStatus.ACCEPTED);
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    @DisplayName("매칭 수락 실패 - 권한 없음")
    void acceptMatch_Unauthorized() {
        // given
        given(matchRepository.findById("match123")).willReturn(Optional.of(match));

        // when & then
        assertThatThrownBy(() -> matchingService.acceptMatch("match123", "userC"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unauthorized to accept this match");
    }

    @Test
    @DisplayName("매칭 거절 성공")
    void rejectMatch_Success() {
        // given
        given(matchRepository.findById("match123")).willReturn(Optional.of(match));
        
        Match rejectedMatch = Match.builder()
                .id("match123")
                .userAId("userA")
                .userBId("userB")
                .score(0.85)
                .matchedAt(LocalDateTime.now())
                .status(Match.MatchStatus.REJECTED)
                .build();
        
        given(matchRepository.save(any(Match.class))).willReturn(rejectedMatch);

        // when
        Match result = matchingService.rejectMatch("match123", "userB");

        // then
        assertThat(result.getStatus()).isEqualTo(Match.MatchStatus.REJECTED);
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    @DisplayName("사용자 매칭 목록 조회")
    void getUserMatches_Success() {
        // given
        List<Match> matches = List.of(match);
        given(matchRepository.findByUserAIdOrUserBId("userA", "userA")).willReturn(matches);

        // when
        List<Match> result = matchingService.getUserMatches("userA");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(match);
    }
}
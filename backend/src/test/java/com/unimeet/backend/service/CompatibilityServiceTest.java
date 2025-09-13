package com.unimeet.backend.service;

import com.unimeet.backend.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CompatibilityServiceTest {

    @InjectMocks
    private CompatibilityService compatibilityService;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        userA = User.builder()
                .id("user1")
                .email("user1@test.com")
                .name("User A")
                .mbti("INTJ")
                .interests(List.of("독서", "영화감상", "운동"))
                .personalityKeywords(List.of("내향적", "논리적", "계획적"))
                .idealType(User.IdealType.builder()
                        .mbti("ENFP")
                        .ageRange("25-30")
                        .personalityKeywords(List.of("외향적", "창의적"))
                        .build())
                .build();

        userB = User.builder()
                .id("user2")
                .email("user2@test.com")
                .name("User B")
                .mbti("ENFP")
                .interests(List.of("독서", "여행", "음악"))
                .personalityKeywords(List.of("외향적", "창의적", "자유로운"))
                .idealType(User.IdealType.builder()
                        .mbti("INTJ")
                        .ageRange("25-35")
                        .personalityKeywords(List.of("내향적", "논리적"))
                        .build())
                .build();
    }

    @Test
    @DisplayName("호환성 점수 계산 - 높은 호환성")
    void calculateCompatibility_HighCompatibility() {
        // when
        double score = compatibilityService.calculateCompatibility(userA, userB);

        // then
        assertThat(score).isGreaterThan(0.7);
        assertThat(score).isLessThanOrEqualTo(1.0);
    }

    @Test
    @DisplayName("호환성 점수 계산 - 동일한 사용자")
    void calculateCompatibility_SameUser() {
        // when
        double score = compatibilityService.calculateCompatibility(userA, userA);

        // then
        assertThat(score).isGreaterThan(0.6);
    }

    @Test
    @DisplayName("호환성 점수 계산 - 낮은 호환성")
    void calculateCompatibility_LowCompatibility() {
        // given
        User userC = User.builder()
                .id("user3")
                .mbti("ESTJ")
                .interests(List.of("게임", "쇼핑"))
                .personalityKeywords(List.of("외향적", "현실적"))
                .idealType(User.IdealType.builder()
                        .mbti("ESFP")
                        .personalityKeywords(List.of("활발한", "사교적"))
                        .build())
                .build();

        // when
        double score = compatibilityService.calculateCompatibility(userA, userC);

        // then
        assertThat(score).isLessThan(0.7);
        assertThat(score).isGreaterThanOrEqualTo(0.0);
    }

    @Test
    @DisplayName("MBTI 호환성 - 완벽한 매칭")
    void calculateCompatibility_PerfectMbtiMatch() {
        // given - INTJ와 ENFP는 호환성이 높음
        
        // when
        double score = compatibilityService.calculateCompatibility(userA, userB);

        // then
        assertThat(score).isGreaterThan(0.7);
    }

    @Test
    @DisplayName("관심사 호환성 - 공통 관심사 있음")
    void calculateCompatibility_CommonInterests() {
        // given - 둘 다 "독서"라는 공통 관심사가 있음
        
        // when
        double score = compatibilityService.calculateCompatibility(userA, userB);

        // then
        assertThat(score).isGreaterThan(0.5);
    }

    @Test
    @DisplayName("null 값 처리")
    void calculateCompatibility_NullValues() {
        // given
        User userWithNulls = User.builder()
                .id("user4")
                .mbti(null)
                .interests(null)
                .personalityKeywords(null)
                .idealType(null)
                .build();

        // when
        double score = compatibilityService.calculateCompatibility(userA, userWithNulls);

        // then
        assertThat(score).isGreaterThanOrEqualTo(0.0);
        assertThat(score).isLessThanOrEqualTo(1.0);
    }

    @Test
    @DisplayName("빈 리스트 처리")
    void calculateCompatibility_EmptyLists() {
        // given
        User userWithEmptyLists = User.builder()
                .id("user5")
                .mbti("ISFP")
                .interests(List.of())
                .personalityKeywords(List.of())
                .idealType(User.IdealType.builder()
                        .mbti("ENFJ")
                        .personalityKeywords(List.of())
                        .build())
                .build();

        // when
        double score = compatibilityService.calculateCompatibility(userA, userWithEmptyLists);

        // then
        assertThat(score).isGreaterThanOrEqualTo(0.0);
        assertThat(score).isLessThanOrEqualTo(1.0);
    }
}
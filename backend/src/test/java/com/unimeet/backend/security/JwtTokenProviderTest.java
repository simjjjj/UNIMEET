package com.unimeet.backend.security;

import com.unimeet.backend.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private User testUser;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        String secret = "test-secret-key-for-jwt-token-must-be-at-least-32-characters-long";
        long expiration = 86400000; // 24시간
        
        jwtTokenProvider = new JwtTokenProvider(secret, expiration);
        
        testUser = User.builder()
                .id("user1")
                .email("test@example.com")
                .password("encodedPassword")
                .name("Test User")
                .role("USER")
                .enabled(true)
                .build();
        
        authentication = new UsernamePasswordAuthenticationToken(
                testUser, null, testUser.getAuthorities());
    }

    @Test
    @DisplayName("JWT 토큰 생성")
    void createToken() {
        // when
        String token = jwtTokenProvider.createToken(authentication);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT는 3개 부분으로 구성
    }

    @Test
    @DisplayName("JWT 토큰에서 사용자명 추출")
    void getUsername() {
        // given
        String token = jwtTokenProvider.createToken(authentication);

        // when
        String username = jwtTokenProvider.getUsername(token);

        // then
        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("유효한 JWT 토큰 검증")
    void validateToken_Valid() {
        // given
        String token = jwtTokenProvider.createToken(authentication);

        // when
        boolean isValid = jwtTokenProvider.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("잘못된 JWT 토큰 검증")
    void validateToken_Invalid() {
        // given
        String invalidToken = "invalid.jwt.token";

        // when
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("빈 JWT 토큰 검증")
    void validateToken_Empty() {
        // when
        boolean isValid = jwtTokenProvider.validateToken("");

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("null JWT 토큰 검증")
    void validateToken_Null() {
        // when
        boolean isValid = jwtTokenProvider.validateToken(null);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("토큰 생성 후 사용자명 추출 일관성 확인")
    void createTokenAndExtractUsername_Consistency() {
        // given
        User anotherUser = User.builder()
                .id("user2")
                .email("another@example.com")
                .password("password")
                .name("Another User")
                .role("USER")
                .enabled(true)
                .build();
        
        Authentication anotherAuth = new UsernamePasswordAuthenticationToken(
                anotherUser, null, anotherUser.getAuthorities());

        // when
        String token1 = jwtTokenProvider.createToken(authentication);
        String token2 = jwtTokenProvider.createToken(anotherAuth);
        
        String username1 = jwtTokenProvider.getUsername(token1);
        String username2 = jwtTokenProvider.getUsername(token2);

        // then
        assertThat(username1).isEqualTo("test@example.com");
        assertThat(username2).isEqualTo("another@example.com");
        assertThat(username1).isNotEqualTo(username2);
    }

    @Test
    @DisplayName("토큰 유효성 검증 - 여러 토큰")
    void validateMultipleTokens() {
        // given
        String token1 = jwtTokenProvider.createToken(authentication);
        
        User user2 = User.builder()
                .email("user2@example.com")
                .role("USER")
                .enabled(true)
                .build();
        Authentication auth2 = new UsernamePasswordAuthenticationToken(
                user2, null, user2.getAuthorities());
        String token2 = jwtTokenProvider.createToken(auth2);

        // when & then
        assertThat(jwtTokenProvider.validateToken(token1)).isTrue();
        assertThat(jwtTokenProvider.validateToken(token2)).isTrue();
        
        assertThat(jwtTokenProvider.getUsername(token1)).isEqualTo("test@example.com");
        assertThat(jwtTokenProvider.getUsername(token2)).isEqualTo("user2@example.com");
    }
}
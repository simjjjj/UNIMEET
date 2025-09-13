package com.unimeet.backend.service;

import com.unimeet.backend.domain.User;
import com.unimeet.backend.dto.SignUpRequest;
import com.unimeet.backend.dto.UpdateProfileRequest;
import com.unimeet.backend.exception.DuplicateEmailException;
import com.unimeet.backend.exception.UserNotFoundException;
import com.unimeet.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private SignUpRequest signUpRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setName("테스트 사용자");
        signUpRequest.setNickname("테스터");
        signUpRequest.setPhone("010-1234-5678");
        signUpRequest.setMbti("INTJ");
        signUpRequest.setInterests(List.of("독서", "영화감상"));
        signUpRequest.setPersonalityKeywords(List.of("내향적", "논리적"));

        SignUpRequest.IdealTypeDto idealType = new SignUpRequest.IdealTypeDto();
        idealType.setMbti("ENFP");
        idealType.setAgeRange("25-30");
        idealType.setPersonalityKeywords(List.of("외향적", "창의적"));
        signUpRequest.setIdealType(idealType);

        user = User.builder()
                .id("user123")
                .email("test@example.com")
                .password("encodedPassword")
                .name("테스트 사용자")
                .nickname("테스터")
                .phone("010-1234-5678")
                .mbti("INTJ")
                .interests(List.of("독서", "영화감상"))
                .personalityKeywords(List.of("내향적", "논리적"))
                .idealType(User.IdealType.builder()
                        .mbti("ENFP")
                        .ageRange("25-30")
                        .personalityKeywords(List.of("외향적", "창의적"))
                        .build())
                .role("USER")
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        User result = userService.signUp(signUpRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("테스트 사용자");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUp_DuplicateEmail() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(signUpRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 존재하는 이메일입니다");
    }

    @Test
    @DisplayName("사용자 ID로 조회 성공")
    void getUserById_Success() {
        // given
        given(userRepository.findById("user123")).willReturn(Optional.of(user));

        // when
        User result = userService.getUserById("user123");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("user123");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("사용자 ID로 조회 실패 - 사용자 없음")
    void getUserById_NotFound() {
        // given
        given(userRepository.findById("nonexistent")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById("nonexistent"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: nonexistent");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void getUserByEmail_Success() {
        // given
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));

        // when
        User result = userService.getUserByEmail("test@example.com");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("프로필 업데이트 성공")
    void updateProfile_Success() {
        // given
        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setNickname("새로운닉네임");
        updateRequest.setMbti("ENFP");
        updateRequest.setInterests(List.of("운동", "음악"));

        User updatedUser = User.builder()
                .id("user123")
                .email("test@example.com")
                .nickname("새로운닉네임")
                .mbti("ENFP")
                .interests(List.of("운동", "음악"))
                .build();

        given(userRepository.findById("user123")).willReturn(Optional.of(user));
        given(userRepository.save(any(User.class))).willReturn(updatedUser);

        // when
        User result = userService.updateProfile("user123", updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo("새로운닉네임");
        assertThat(result.getMbti()).isEqualTo("ENFP");
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("프로필 업데이트 실패 - 사용자 없음")
    void updateProfile_UserNotFound() {
        // given
        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setNickname("새로운닉네임");

        given(userRepository.findById("nonexistent")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateProfile("nonexistent", updateRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: nonexistent");
    }
}
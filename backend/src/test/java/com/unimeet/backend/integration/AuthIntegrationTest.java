package com.unimeet.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimeet.backend.TestBase;
import com.unimeet.backend.dto.LoginRequest;
import com.unimeet.backend.dto.SignUpRequest;
import com.unimeet.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthIntegrationTest extends TestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 후 로그인 통합 테스트")
    void signUpAndLogin_Integration() throws Exception {
        // given
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("integration@test.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setPhone("010-1234-5678");
        signUpRequest.setNickname("integrationuser");
        signUpRequest.setName("Integration User");
        signUpRequest.setMbti("INTJ");
        signUpRequest.setInterests(List.of("독서", "영화"));
        signUpRequest.setPersonalityKeywords(List.of("내향적", "논리적"));

        SignUpRequest.IdealTypeDto idealType = new SignUpRequest.IdealTypeDto();
        idealType.setMbti("ENFP");
        idealType.setAgeRange("25-30");
        idealType.setPersonalityKeywords(List.of("외향적", "창의적"));
        signUpRequest.setIdealType(idealType);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("integration@test.com");
        loginRequest.setPassword("password123");

        // when & then - 회원가입
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        // when & then - 로그인
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("integration@test.com"));
    }

    @Test
    @DisplayName("중복 이메일 회원가입 시도")
    void signUpWithDuplicateEmail() throws Exception {
        // given
        SignUpRequest firstRequest = new SignUpRequest();
        firstRequest.setEmail("duplicate@test.com");
        firstRequest.setPassword("password123");
        firstRequest.setPhone("010-1111-1111");
        firstRequest.setNickname("first");
        firstRequest.setName("First User");

        SignUpRequest secondRequest = new SignUpRequest();
        secondRequest.setEmail("duplicate@test.com");
        secondRequest.setPassword("password456");
        secondRequest.setPhone("010-2222-2222");
        secondRequest.setNickname("second");
        secondRequest.setName("Second User");

        // when & then - 첫 번째 회원가입 성공
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());

        // when & then - 두 번째 회원가입 실패 (중복 이메일)
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }

    @Test
    @DisplayName("잘못된 로그인 정보로 로그인 시도")
    void loginWithWrongCredentials() throws Exception {
        // given - 먼저 사용자 등록
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("correctpassword");
        signUpRequest.setPhone("010-1234-5678");
        signUpRequest.setNickname("testuser");
        signUpRequest.setName("Test User");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk());

        // when & then - 잘못된 비밀번호로 로그인
        LoginRequest wrongPasswordRequest = new LoginRequest();
        wrongPasswordRequest.setEmail("test@example.com");
        wrongPasswordRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isBadRequest());

        // when & then - 존재하지 않는 이메일로 로그인
        LoginRequest wrongEmailRequest = new LoginRequest();
        wrongEmailRequest.setEmail("nonexistent@example.com");
        wrongEmailRequest.setPassword("correctpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongEmailRequest)))
                .andExpect(status().isBadRequest());
    }
}
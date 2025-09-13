package com.unimeet.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimeet.backend.TestBase;
import com.unimeet.backend.dto.LoginRequest;
import com.unimeet.backend.dto.SignUpRequest;
import com.unimeet.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class AuthControllerTest extends TestBase {

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
    @DisplayName("회원가입 성공")
    void signUp_Success() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("테스트 사용자");
        request.setNickname("테스터");
        request.setPhone("010-1234-5678");
        request.setMbti("INTJ");
        request.setInterests(List.of("독서", "영화감상"));
        request.setPersonalityKeywords(List.of("내향적", "논리적"));

        SignUpRequest.IdealTypeDto idealType = new SignUpRequest.IdealTypeDto();
        idealType.setMbti("ENFP");
        idealType.setAgeRange("25-30");
        idealType.setPersonalityKeywords(List.of("외향적", "창의적"));
        request.setIdealType(idealType);

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 이메일 형식")
    void signUp_InvalidEmail() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");
        request.setName("테스트 사용자");
        request.setNickname("테스터");

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 실패 - 짧은 비밀번호")
    void signUp_ShortPassword() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest();
        request.setEmail("test@example.com");
        request.setPassword("123");
        request.setName("테스트 사용자");
        request.setNickname("테스터");

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_UserNotFound() throws Exception {
        // given
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password123");

        // when & then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 이메일 형식")
    void login_InvalidEmail() throws Exception {
        // given
        LoginRequest request = new LoginRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");

        // when & then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
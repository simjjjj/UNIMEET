package com.unimeet.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimeet.backend.domain.Match;
import com.unimeet.backend.domain.User;
import com.unimeet.backend.service.MatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchingService matchingService;

    private User currentUser;
    private Match match;
    private UsernamePasswordAuthenticationToken authentication;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id("user1")
                .email("user1@test.com")
                .name("Current User")
                .role("USER")
                .enabled(true)
                .build();

        match = Match.builder()
                .id("match1")
                .userAId("user1")
                .userBId("user2")
                .score(0.8)
                .matchedAt(LocalDateTime.now())
                .status(Match.MatchStatus.PENDING)
                .build();

        authentication = new UsernamePasswordAuthenticationToken(
                currentUser, null, currentUser.getAuthorities());
    }

    @Test
    @DisplayName("매칭 후보 조회 - 성공")
    void getMatchCandidates_Success() throws Exception {
        // given
        List<Match> matches = List.of(match);
        when(matchingService.findMatches("user1", 10)).thenReturn(matches);

        // when & then
        mockMvc.perform(get("/api/matches/candidates")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("match1"))
                .andExpect(jsonPath("$[0].userAId").value("user1"))
                .andExpect(jsonPath("$[0].userBId").value("user2"))
                .andExpect(jsonPath("$[0].score").value(0.8));

        verify(matchingService).findMatches("user1", 10);
    }

    @Test
    @DisplayName("매칭 요청 생성 - 성공")
    void createMatchRequest_Success() throws Exception {
        // given
        Map<String, String> request = Map.of("targetUserId", "user2");
        when(matchingService.createMatch("user1", "user2")).thenReturn(match);

        // when & then
        mockMvc.perform(post("/api/matches/request")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("match1"))
                .andExpect(jsonPath("$.userAId").value("user1"))
                .andExpect(jsonPath("$.userBId").value("user2"));

        verify(matchingService).createMatch("user1", "user2");
    }

    @Test
    @DisplayName("매칭 요청 생성 - 대상 사용자 ID 누락")
    void createMatchRequest_MissingTargetUserId() throws Exception {
        // given
        Map<String, String> request = Map.of();

        // when & then
        mockMvc.perform(post("/api/matches/request")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Target user ID is required"));

        verify(matchingService, never()).createMatch(any(), any());
    }

    @Test
    @DisplayName("매칭 요청 생성 - 이미 존재하는 매칭")
    void createMatchRequest_AlreadyExists() throws Exception {
        // given
        Map<String, String> request = Map.of("targetUserId", "user2");
        when(matchingService.createMatch("user1", "user2"))
                .thenThrow(new RuntimeException("Match already exists"));

        // when & then
        mockMvc.perform(post("/api/matches/request")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Match already exists"));

        verify(matchingService).createMatch("user1", "user2");
    }

    @Test
    @DisplayName("매칭 수락 - 성공")
    void acceptMatch_Success() throws Exception {
        // given
        Match acceptedMatch = Match.builder()
                .id("match1")
                .userAId("user1")
                .userBId("user2")
                .status(Match.MatchStatus.ACCEPTED)
                .build();
        when(matchingService.acceptMatch("match1", "user1")).thenReturn(acceptedMatch);

        // when & then
        mockMvc.perform(post("/api/matches/match1/accept")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("match1"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(matchingService).acceptMatch("match1", "user1");
    }

    @Test
    @DisplayName("매칭 수락 - 권한 없음")
    void acceptMatch_Unauthorized() throws Exception {
        // given
        when(matchingService.acceptMatch("match1", "user1"))
                .thenThrow(new RuntimeException("Unauthorized to accept this match"));

        // when & then
        mockMvc.perform(post("/api/matches/match1/accept")
                        .with(authentication(authentication)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unauthorized to accept this match"));

        verify(matchingService).acceptMatch("match1", "user1");
    }

    @Test
    @DisplayName("매칭 거절 - 성공")
    void rejectMatch_Success() throws Exception {
        // given
        Match rejectedMatch = Match.builder()
                .id("match1")
                .userAId("user1")
                .userBId("user2")
                .status(Match.MatchStatus.REJECTED)
                .build();
        when(matchingService.rejectMatch("match1", "user1")).thenReturn(rejectedMatch);

        // when & then
        mockMvc.perform(post("/api/matches/match1/reject")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("match1"))
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(matchingService).rejectMatch("match1", "user1");
    }

    @Test
    @DisplayName("내 매칭 목록 조회")
    void getMyMatches() throws Exception {
        // given
        List<Match> matches = List.of(match);
        when(matchingService.getUserMatches("user1")).thenReturn(matches);

        // when & then
        mockMvc.perform(get("/api/matches/my-matches")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("match1"));

        verify(matchingService).getUserMatches("user1");
    }

    @Test
    @DisplayName("수락된 매칭 목록 조회")
    void getAcceptedMatches() throws Exception {
        // given
        Match acceptedMatch = Match.builder()
                .id("match1")
                .userAId("user1")
                .userBId("user2")
                .status(Match.MatchStatus.ACCEPTED)
                .build();
        List<Match> acceptedMatches = List.of(acceptedMatch);
        when(matchingService.getAcceptedMatches("user1")).thenReturn(acceptedMatches);

        // when & then
        mockMvc.perform(get("/api/matches/accepted")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("match1"))
                .andExpect(jsonPath("$[0].status").value("ACCEPTED"));

        verify(matchingService).getAcceptedMatches("user1");
    }

    @Test
    @DisplayName("인증되지 않은 사용자 - 401 에러")
    void unauthenticatedUser_Returns401() throws Exception {
        // when & then
        mockMvc.perform(get("/api/matches/candidates"))
                .andExpect(status().isUnauthorized());

        verify(matchingService, never()).findMatches(any(), anyInt());
    }
}
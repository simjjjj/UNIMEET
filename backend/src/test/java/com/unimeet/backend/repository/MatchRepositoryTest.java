package com.unimeet.backend.repository;

import com.unimeet.backend.TestBase;
import com.unimeet.backend.domain.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class MatchRepositoryTest extends TestBase {

    @Autowired
    private MatchRepository matchRepository;

    private Match match1;
    private Match match2;
    private Match match3;

    @BeforeEach
    void setUp() {
        matchRepository.deleteAll();
        
        match1 = Match.builder()
                .userAId("user1")
                .userBId("user2")
                .score(0.8)
                .matchedAt(LocalDateTime.now())
                .status(Match.MatchStatus.PENDING)
                .build();

        match2 = Match.builder()
                .userAId("user2")
                .userBId("user3")
                .score(0.7)
                .matchedAt(LocalDateTime.now())
                .status(Match.MatchStatus.ACCEPTED)
                .build();

        match3 = Match.builder()
                .userAId("user3")
                .userBId("user1")
                .score(0.6)
                .matchedAt(LocalDateTime.now())
                .status(Match.MatchStatus.REJECTED)
                .build();
    }

    @Test
    @DisplayName("매칭 저장 및 조회")
    void saveAndFindMatch() {
        // when
        Match savedMatch = matchRepository.save(match1);

        // then
        assertThat(savedMatch.getId()).isNotNull();
        assertThat(savedMatch.getUserAId()).isEqualTo("user1");
        assertThat(savedMatch.getUserBId()).isEqualTo("user2");
        assertThat(savedMatch.getScore()).isEqualTo(0.8);
        assertThat(savedMatch.getStatus()).isEqualTo(Match.MatchStatus.PENDING);
    }

    @Test
    @DisplayName("사용자 ID로 매칭 찾기")
    void findByUserAIdOrUserBId() {
        // given
        matchRepository.save(match1);
        matchRepository.save(match2);
        matchRepository.save(match3);

        // when
        List<Match> user1Matches = matchRepository.findByUserAIdOrUserBId("user1", "user1");

        // then
        assertThat(user1Matches).hasSize(2);
        assertThat(user1Matches).extracting("userAId", "userBId")
                .containsExactlyInAnyOrder(
                        tuple("user1", "user2"),
                        tuple("user3", "user1")
                );
    }

    @Test
    @DisplayName("상태별 매칭 찾기")
    void findByStatus() {
        // given
        matchRepository.save(match1);
        matchRepository.save(match2);
        matchRepository.save(match3);

        // when
        List<Match> pendingMatches = matchRepository.findByStatus(Match.MatchStatus.PENDING);
        List<Match> acceptedMatches = matchRepository.findByStatus(Match.MatchStatus.ACCEPTED);
        List<Match> rejectedMatches = matchRepository.findByStatus(Match.MatchStatus.REJECTED);

        // then
        assertThat(pendingMatches).hasSize(1);
        assertThat(acceptedMatches).hasSize(1);
        assertThat(rejectedMatches).hasSize(1);
        
        assertThat(pendingMatches.get(0).getUserAId()).isEqualTo("user1");
        assertThat(acceptedMatches.get(0).getUserAId()).isEqualTo("user2");
        assertThat(rejectedMatches.get(0).getUserAId()).isEqualTo("user3");
    }

    @Test
    @DisplayName("두 사용자 간 매칭 찾기")
    void findByUserAIdAndUserBIdOrUserBIdAndUserAId() {
        // given
        matchRepository.save(match1);
        matchRepository.save(match2);

        // when
        Optional<Match> found1 = matchRepository.findByUserAIdAndUserBIdOrUserBIdAndUserAId(
                "user1", "user2", "user1", "user2");
        Optional<Match> found2 = matchRepository.findByUserAIdAndUserBIdOrUserBIdAndUserAId(
                "user2", "user1", "user2", "user1");
        Optional<Match> notFound = matchRepository.findByUserAIdAndUserBIdOrUserBIdAndUserAId(
                "user1", "user4", "user1", "user4");

        // then
        assertThat(found1).isPresent();
        assertThat(found2).isPresent();
        assertThat(notFound).isEmpty();
        
        assertThat(found1.get().getUserAId()).isEqualTo("user1");
        assertThat(found1.get().getUserBId()).isEqualTo("user2");
        assertThat(found2.get().getUserAId()).isEqualTo("user1");
        assertThat(found2.get().getUserBId()).isEqualTo("user2");
    }

    @Test
    @DisplayName("사용자 ID와 상태로 매칭 찾기")
    void findByUserAIdOrUserBIdAndStatus() {
        // given
        matchRepository.save(match1);
        matchRepository.save(match2);
        matchRepository.save(match3);

        // when
        List<Match> user1AcceptedMatches = matchRepository.findByUserAIdOrUserBIdAndStatus(
                "user1", "user1", Match.MatchStatus.ACCEPTED);
        List<Match> user2AcceptedMatches = matchRepository.findByUserAIdOrUserBIdAndStatus(
                "user2", "user2", Match.MatchStatus.ACCEPTED);

        // then
        assertThat(user1AcceptedMatches).isEmpty();
        assertThat(user2AcceptedMatches).hasSize(1);
        assertThat(user2AcceptedMatches.get(0).getUserAId()).isEqualTo("user2");
        assertThat(user2AcceptedMatches.get(0).getUserBId()).isEqualTo("user3");
    }

    @Test
    @DisplayName("매칭 상태 업데이트")
    void updateMatchStatus() {
        // given
        Match savedMatch = matchRepository.save(match1);
        String matchId = savedMatch.getId();

        // when
        savedMatch.setStatus(Match.MatchStatus.ACCEPTED);
        Match updatedMatch = matchRepository.save(savedMatch);

        // then
        assertThat(updatedMatch.getId()).isEqualTo(matchId);
        assertThat(updatedMatch.getStatus()).isEqualTo(Match.MatchStatus.ACCEPTED);
        
        Optional<Match> found = matchRepository.findById(matchId);
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(Match.MatchStatus.ACCEPTED);
    }

    @Test
    @DisplayName("매칭 삭제")
    void deleteMatch() {
        // given
        Match savedMatch = matchRepository.save(match1);
        String matchId = savedMatch.getId();

        // when
        matchRepository.deleteById(matchId);

        // then
        Optional<Match> found = matchRepository.findById(matchId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("모든 매칭 조회")
    void findAllMatches() {
        // given
        matchRepository.save(match1);
        matchRepository.save(match2);
        matchRepository.save(match3);

        // when
        List<Match> allMatches = matchRepository.findAll();

        // then
        assertThat(allMatches).hasSize(3);
        assertThat(allMatches).extracting("status")
                .containsExactlyInAnyOrder(
                        Match.MatchStatus.PENDING,
                        Match.MatchStatus.ACCEPTED,
                        Match.MatchStatus.REJECTED
                );
    }

    private static org.assertj.core.groups.Tuple tuple(Object... values) {
        return org.assertj.core.groups.Tuple.tuple(values);
    }
}
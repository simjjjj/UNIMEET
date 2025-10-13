package com.unimeet.backend.repository;

import com.unimeet.backend.domain.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends MongoRepository<Match, String> {
    // 기존 매칭 시스템용
    List<Match> findByUserAIdOrUserBId(String userAId, String userBId);
    Page<Match> findByUserAIdOrUserBId(String userAId, String userBId, Pageable pageable);
    
    List<Match> findByStatus(Match.MatchStatus status);
    Page<Match> findByStatus(Match.MatchStatus status, Pageable pageable);
    
    @Query("{ $or: [ { 'userAId': ?0, 'userBId': ?1 }, { 'userAId': ?2, 'userBId': ?3 } ] }")
    Optional<Match> findByUserAIdAndUserBIdOrUserBIdAndUserAId(String userAId1, String userBId1, String userAId2, String userBId2);
    
    @Query("{ $or: [ { 'userAId': ?0 }, { 'userBId': ?1 } ], 'status': ?2 }")
    List<Match> findByUserAIdOrUserBIdAndStatus(String userAId, String userBId, Match.MatchStatus status);
    
    @Query("{ $or: [ { 'userAId': ?0 }, { 'userBId': ?1 } ], 'status': ?2 }")
    Page<Match> findByUserAIdOrUserBIdAndStatus(String userAId, String userBId, Match.MatchStatus status, Pageable pageable);
    
    // 새 매칭 시스템용 (requesterId, targetId 기반)
    Optional<Match> findByRequesterIdAndTargetId(String requesterId, String targetId);
    List<Match> findByTargetIdOrderByCreatedAtDesc(String targetId);
    List<Match> findByRequesterIdOrderByCreatedAtDesc(String requesterId);
    
    @Query("{'status': ?0, '$or': [{'requesterId': ?1}, {'targetId': ?1}]}")
    List<Match> findByStatusAndUserIdOrderByRespondedAtDesc(Match.MatchStatus status, String userId);
} 
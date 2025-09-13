package com.unimeet.backend.service;

import com.unimeet.backend.domain.User;
import com.unimeet.backend.dto.SignUpRequest;
import com.unimeet.backend.dto.UpdateProfileRequest;
import com.unimeet.backend.exception.DuplicateEmailException;
import com.unimeet.backend.exception.UserNotFoundException;
import com.unimeet.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다");
        }

        LocalDateTime now = LocalDateTime.now();
        
        User user = User.builder()
                // 기본 정보
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .studentId(request.getStudentId())
                .department(request.getDepartment())
                .birth(request.getBirth())
                .phone(request.getPhone())
                
                // 프로필 정보 (선택사항)
                .mbti(request.getMbti())
                .interests(request.getInterests())
                .height(request.getHeight())
                
                // 기존 필드들 (호환성 유지)
                .personalityKeywords(request.getPersonalityKeywords())
                .idealType(request.getIdealType() != null ? User.IdealType.builder()
                        .mbti(request.getIdealType().getMbti())
                        .ageRange(request.getIdealType().getAgeRange())
                        .personalityKeywords(request.getIdealType().getPersonalityKeywords())
                        .build() : null)
                
                // 시스템 필드
                .role("USER")
                .enabled(true)
                .isVerified(false) // 이메일 인증 필요
                .createdAt(now)
                .updatedAt(now)
                .build();

        User savedUser = userRepository.save(user);
        log.info("새 사용자 가입: {} ({})", savedUser.getName(), savedUser.getEmail());
        
        return savedUser;
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "users", key = "#userId")
    public User updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // 업데이트할 필드들만 변경
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        
        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }
        
        if (StringUtils.hasText(request.getStudentId())) {
            user.setStudentId(request.getStudentId());
        }
        
        if (StringUtils.hasText(request.getDepartment())) {
            user.setDepartment(request.getDepartment());
        }
        
        if (StringUtils.hasText(request.getBirth())) {
            user.setBirth(request.getBirth());
        }
        
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        
        if (StringUtils.hasText(request.getMbti())) {
            user.setMbti(request.getMbti());
        }
        
        if (request.getInterests() != null) {
            user.setInterests(request.getInterests());
        }
        
        if (StringUtils.hasText(request.getHeight())) {
            user.setHeight(request.getHeight());
        }
        
        if (request.getPersonalityKeywords() != null) {
            user.setPersonalityKeywords(request.getPersonalityKeywords());
        }
        
        if (request.getIdealType() != null) {
            User.IdealType idealType = User.IdealType.builder()
                    .mbti(request.getIdealType().getMbti())
                    .ageRange(request.getIdealType().getAgeRange())
                    .personalityKeywords(request.getIdealType().getPersonalityKeywords())
                    .build();
            user.setIdealType(idealType);
        }

        // 수정 시간 업데이트
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        log.info("사용자 프로필 업데이트: {} ({})", updatedUser.getName(), userId);
        
        return updatedUser;
    }

    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "users", key = "#userId")
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "users", key = "#email")
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
} 
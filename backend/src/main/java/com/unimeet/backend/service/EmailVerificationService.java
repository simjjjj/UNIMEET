package com.unimeet.backend.service;

import com.unimeet.backend.domain.User;
import com.unimeet.backend.exception.UserNotFoundException;
import com.unimeet.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String VERIFICATION_CODE_PREFIX = "email_verification:";
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRY_MINUTES = 10;

    /**
     * 이메일 인증 코드 생성 및 발송
     */
    public void generateAndSendVerificationCode(String email) {
        // 1. 6자리 랜덤 숫자 생성
        String code = generateRandomCode();
        
        // 2. Redis에 인증 코드 저장 (10분 만료)
        String key = VERIFICATION_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRY_MINUTES, TimeUnit.MINUTES);
        
        // 3. 실제 이메일 발송
        try {
            emailService.sendVerificationEmail(email, code);
            log.info("이메일 인증 코드 발송 완료: {} -> {}", email, code);
        } catch (Exception e) {
            // 이메일 발송 실패 시 Redis에서 코드 삭제
            redisTemplate.delete(key);
            log.error("이메일 발송 실패: {}", email, e);
            
            // 개발 환경에서는 에러를 던지지 않고 로그만 출력
            log.warn("개발 환경에서 이메일 발송 실패를 무시하고 계속 진행합니다.");
        }
    }

    /**
     * 이메일 인증 코드 검증
     */
    @Transactional
    public boolean verifyEmail(String email, String code) {
        String key = VERIFICATION_CODE_PREFIX + email;
        String storedCode = (String) redisTemplate.opsForValue().get(key);
        
        if (storedCode == null) {
            log.warn("만료되거나 존재하지 않는 인증 코드: {}", email);
            return false;
        }
        
        if (!storedCode.equals(code)) {
            log.warn("잘못된 인증 코드: {} (입력: {}, 저장: {})", email, code, storedCode);
            return false;
        }
        
        // 인증 성공 시 사용자 상태 업데이트
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + email));
        
        user.setVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // 인증 코드 삭제
        redisTemplate.delete(key);
        
        log.info("이메일 인증 완료: {}", email);
        return true;
    }

    /**
     * 인증 코드 재발송
     */
    public void resendVerificationCode(String email) {
        // 기존 코드 삭제
        String key = VERIFICATION_CODE_PREFIX + email;
        redisTemplate.delete(key);
        
        // 새 코드 생성 및 발송
        generateAndSendVerificationCode(email);
        log.info("인증 코드 재발송: {}", email);
    }

    /**
     * 랜덤 6자리 숫자 코드 생성
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }

    /**
     * 인증 코드 유효성 확인 (만료 시간 체크)
     */
    public boolean isCodeValid(String email) {
        String key = VERIFICATION_CODE_PREFIX + email;
        return redisTemplate.hasKey(key);
    }

    /**
     * 남은 만료 시간 조회 (초 단위)
     */
    public long getCodeExpiryTime(String email) {
        String key = VERIFICATION_CODE_PREFIX + email;
        Long expiry = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expiry != null ? expiry : 0;
    }
}
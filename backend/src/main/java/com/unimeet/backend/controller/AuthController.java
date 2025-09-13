package com.unimeet.backend.controller;

import com.unimeet.backend.dto.LoginRequest;
import com.unimeet.backend.dto.LoginResponse;
import com.unimeet.backend.dto.SignUpRequest;
import jakarta.validation.Valid;
import com.unimeet.backend.security.JwtTokenProvider;
import com.unimeet.backend.service.EmailVerificationService;
import com.unimeet.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:19006"})
public class AuthController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        try {
            // 건국대학교 이메일 도메인 검증
            if (!request.getEmail().toLowerCase().endsWith("@kku.ac.kr")) {
                return ResponseEntity.badRequest().body("건국대학교 이메일(@kku.ac.kr)만 사용 가능합니다");
            }
            
            userService.signUp(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "회원가입이 완료되었습니다");
            response.put("email", request.getEmail());
            response.put("nextStep", "이메일 인증을 진행해주세요");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // JWT 토큰 생성
            String token = jwtTokenProvider.createToken(authentication);
            
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .email(request.getEmail())
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 이메일 인증 코드 발송
     */
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("이메일은 필수입니다");
            }
            
            // 건국대학교 이메일 도메인 검증
            if (!email.toLowerCase().endsWith("@kku.ac.kr")) {
                return ResponseEntity.badRequest().body("건국대학교 이메일(@kku.ac.kr)만 사용 가능합니다");
            }
            
            emailVerificationService.generateAndSendVerificationCode(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "인증 코드가 발송되었습니다");
            response.put("email", email);
            response.put("expiryMinutes", 10);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("이메일 인증 코드 발송 실패", e);
            return ResponseEntity.status(500).body("이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    /**
     * 이메일 인증 코드 확인
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String code = request.get("code");
            
            if (email == null || code == null) {
                return ResponseEntity.badRequest().body("이메일과 인증 코드는 필수입니다");
            }
            
            boolean isVerified = emailVerificationService.verifyEmail(email, code);
            
            if (isVerified) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "이메일 인증이 완료되었습니다");
                response.put("email", email);
                response.put("verified", true);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("잘못된 인증 코드이거나 만료된 코드입니다");
            }
        } catch (Exception e) {
            log.error("이메일 인증 실패", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 인증 코드 재발송
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("이메일은 필수입니다");
            }
            
            // 건국대학교 이메일 도메인 검증
            if (!email.toLowerCase().endsWith("@kku.ac.kr")) {
                return ResponseEntity.badRequest().body("건국대학교 이메일(@kku.ac.kr)만 사용 가능합니다");
            }
            
            emailVerificationService.resendVerificationCode(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "인증 코드가 재발송되었습니다");
            response.put("email", email);
            response.put("expiryMinutes", 10);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("인증 코드 재발송 실패", e);
            return ResponseEntity.status(500).body("이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    /**
     * 인증 코드 상태 확인
     */
    @GetMapping("/verification-status")
    public ResponseEntity<?> getVerificationStatus(@RequestParam String email) {
        try {
            boolean isValid = emailVerificationService.isCodeValid(email);
            long expirySeconds = emailVerificationService.getCodeExpiryTime(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("hasValidCode", isValid);
            response.put("expirySeconds", expirySeconds);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 
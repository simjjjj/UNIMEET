package com.unimeet.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SignUpRequest {
    // 회원가입 기본 정보 (필수)
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;
    
    @NotBlank(message = "이름은 필수입니다")
    private String name;
    
    @NotBlank(message = "닉네임은 필수입니다")
    private String nickname;
    
    @NotBlank(message = "학번은 필수입니다")
    private String studentId;
    
    @NotBlank(message = "학과는 필수입니다")
    private String department;
    
    @NotBlank(message = "생년월일은 필수입니다")
    private String birth;
    
    @NotBlank(message = "전화번호는 필수입니다")
    private String phone;
    
    // 온보딩 프로필 정보 (선택사항)
    private String mbti;
    private List<String> interests;
    private String height;
    
    // 기존 필드들 (호환성 유지)
    private List<String> personalityKeywords;
    
    @Valid
    private IdealTypeDto idealType;

    @Data
    public static class IdealTypeDto {
        private String mbti;
        private String ageRange;
        private List<String> personalityKeywords;
    }
} 
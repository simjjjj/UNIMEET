package com.unimeet.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProfileRequest {
    
    // 기본 정보
    @Size(max = 50, message = "닉네임은 50자를 초과할 수 없습니다")
    private String nickname;
    
    @Size(max = 100, message = "이름은 100자를 초과할 수 없습니다")
    private String name;
    
    private String studentId;
    private String department;
    private String birth;
    private String phone;
    
    // 프로필 정보
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
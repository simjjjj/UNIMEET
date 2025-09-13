package com.unimeet.backend.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private String id;
    
    // 회원가입 기본 정보
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String studentId;        // 학번
    private String department;       // 학과
    private String birth;           // 생년월일
    private String phone;           // 전화번호 (phoneNumber -> phone으로 변경)
    
    // 온보딩 프로필 정보 (선택사항)
    private String mbti;
    private List<String> interests;
    private String height;          // 키
    
    // 기존 필드들 (호환성 유지)
    private List<String> personalityKeywords;
    private IdealType idealType;
    
    // 시스템 필드
    private String role;
    private boolean enabled;
    private boolean isVerified;     // 이메일 인증 여부
    private LocalDateTime createdAt; // 가입일시
    private LocalDateTime updatedAt; // 수정일시
    
    @Data
    @Builder
    public static class IdealType {
        private String mbti;
        private String ageRange;
        private List<String> personalityKeywords;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
} 
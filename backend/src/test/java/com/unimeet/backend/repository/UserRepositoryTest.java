package com.unimeet.backend.repository;

import com.unimeet.backend.TestBase;
import com.unimeet.backend.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class UserRepositoryTest extends TestBase {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        testUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .phone("010-1234-5678")
                .nickname("testuser")
                .name("Test User")
                .mbti("INTJ")
                .interests(List.of("독서", "영화"))
                .personalityKeywords(List.of("내향적", "논리적"))
                .idealType(User.IdealType.builder()
                        .mbti("ENFP")
                        .ageRange("25-30")
                        .personalityKeywords(List.of("외향적", "창의적"))
                        .build())
                .role("USER")
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("사용자 저장 및 조회")
    void saveAndFindUser() {
        // when
        User savedUser = userRepository.save(testUser);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getMbti()).isEqualTo("INTJ");
        assertThat(savedUser.getInterests()).contains("독서", "영화");
        assertThat(savedUser.getPersonalityKeywords()).contains("내향적", "논리적");
        assertThat(savedUser.getIdealType()).isNotNull();
        assertThat(savedUser.getIdealType().getMbti()).isEqualTo("ENFP");
    }

    @Test
    @DisplayName("이메일로 사용자 찾기 - 존재하는 경우")
    void findByEmail_Exists() {
        // given
        userRepository.save(testUser);

        // when
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("이메일로 사용자 찾기 - 존재하지 않는 경우")
    void findByEmail_NotExists() {
        // when
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하는 경우")
    void existsByEmail_Exists() {
        // given
        userRepository.save(testUser);

        // when
        boolean exists = userRepository.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하지 않는 경우")
    void existsByEmail_NotExists() {
        // when
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("여러 사용자 저장 및 조회")
    void saveAndFindMultipleUsers() {
        // given
        User user2 = User.builder()
                .email("user2@example.com")
                .password("password2")
                .phone("010-9876-5432")
                .nickname("user2")
                .name("User Two")
                .mbti("ENFP")
                .role("USER")
                .enabled(true)
                .build();

        // when
        userRepository.save(testUser);
        userRepository.save(user2);

        // then
        List<User> allUsers = userRepository.findAll();
        assertThat(allUsers).hasSize(2);
        
        Optional<User> foundUser1 = userRepository.findByEmail("test@example.com");
        Optional<User> foundUser2 = userRepository.findByEmail("user2@example.com");
        
        assertThat(foundUser1).isPresent();
        assertThat(foundUser2).isPresent();
        assertThat(foundUser1.get().getMbti()).isEqualTo("INTJ");
        assertThat(foundUser2.get().getMbti()).isEqualTo("ENFP");
    }

    @Test
    @DisplayName("사용자 업데이트")
    void updateUser() {
        // given
        User savedUser = userRepository.save(testUser);
        String userId = savedUser.getId();

        // when
        savedUser.setNickname("updatedNickname");
        savedUser.setMbti("ENFP");
        User updatedUser = userRepository.save(savedUser);

        // then
        assertThat(updatedUser.getId()).isEqualTo(userId);
        assertThat(updatedUser.getNickname()).isEqualTo("updatedNickname");
        assertThat(updatedUser.getMbti()).isEqualTo("ENFP");
        
        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isPresent();
        assertThat(found.get().getNickname()).isEqualTo("updatedNickname");
        assertThat(found.get().getMbti()).isEqualTo("ENFP");
    }

    @Test
    @DisplayName("사용자 삭제")
    void deleteUser() {
        // given
        User savedUser = userRepository.save(testUser);
        String userId = savedUser.getId();

        // when
        userRepository.deleteById(userId);

        // then
        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isEmpty();
        
        boolean exists = userRepository.existsByEmail("test@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("이상형 정보가 null인 사용자 저장")
    void saveUserWithNullIdealType() {
        // given
        User userWithoutIdealType = User.builder()
                .email("noideal@example.com")
                .password("password")
                .phone("010-1111-1111")
                .nickname("noideal")
                .name("No Ideal User")
                .mbti("ISFP")
                .idealType(null)
                .role("USER")
                .enabled(true)
                .build();

        // when
        User savedUser = userRepository.save(userWithoutIdealType);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getIdealType()).isNull();
        
        Optional<User> found = userRepository.findByEmail("noideal@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getIdealType()).isNull();
    }
}
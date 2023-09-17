package com.choi.doit.domain.mypage.application;

import com.choi.doit.domain.mypage.dto.request.EditEmailRequestDto;
import com.choi.doit.domain.mypage.dto.request.EditPasswordRequestDto;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import com.choi.doit.global.util.RedisUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MyPage Service Test")
@Transactional
@Slf4j
@SpringBootTest
class MyPageServiceTest {
    final MyPageService myPageService;
    final UserRepository userRepository;
    final RedisUtil redisUtil;
    final PasswordEncoder passwordEncoder;
    final String email = "abc@abc.com";
    final String password = "password1234";
    final String nickname = "genius";

    @Autowired
    MyPageServiceTest(MyPageService myPageService, UserRepository userRepository, RedisUtil redisUtil, PasswordEncoder passwordEncoder) {
        this.myPageService = myPageService;
        this.userRepository = userRepository;
        this.redisUtil = redisUtil;
        this.passwordEncoder = passwordEncoder;
    }

    void addData() {
        userRepository.save(new EmailJoinRequestDto(email, password, nickname).toEntity());
    }

    @DisplayName("계정 삭제")
    @WithMockUser(username = email)
    @Test
    void deleteUser() {
        // given
        Long id = userRepository.save(new EmailJoinRequestDto(email, password, nickname).toEntity()).getId();
        String KEY_SUFFIX = "_refresh";

        // when
        myPageService.deleteUser();

        // then
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        String value = redisUtil.opsForValueGet(id + KEY_SUFFIX);
        assertThat(user).isNull();
        assertThat(value).isNull();
    }

    @DisplayName("프로필 이미지 변경")
    @WithMockUser(username = email)
    @Test
    void setProfileImage() throws IOException {
        // given
        MockMultipartFile file = new MockMultipartFile("profile", "car.jpeg", "image/jpeg", new ClassPathResource("car.jpeg").getInputStream());
        UserEntity user = userRepository.save(new EmailJoinRequestDto(email, password, nickname).toEntity());

        // when
        myPageService.setProfileImage(file);

        // then
        assertThat(user.getProfile_image_path()).isNotNull();
    }

    @DisplayName("이메일 변경")
    @WithMockUser(username = email)
    @Test
    void setEmail() {
        // given
        UserEntity user = userRepository.save(new EmailJoinRequestDto(email, password, nickname).toEntity());
        String new_email = "new_email@abc.com";

        // when
        myPageService.setEmail(new EditEmailRequestDto(new_email));

        // then
        assertThat(user.getEmail()).isEqualTo(new_email);
    }

    @DisplayName("비밀번호 변경")
    @WithMockUser(username = email)
    @Test
    void setPassword() throws Exception {
        // given
        UserEntity user = userRepository.save(new EmailJoinRequestDto(email, password, nickname).toEntity());
        String new_password = "newPassword123@";

        // when
        myPageService.setPassword(new EditPasswordRequestDto(new_password));

        // then
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found."));
        assertThat(passwordEncoder.matches(password, user.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(new_password, user.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(password, userEntity.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(new_password, userEntity.getPassword())).isTrue();
    }
}
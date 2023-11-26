package com.choi.doit.domain.mypage.api;

import com.choi.doit.domain.mypage.dto.request.EditEmailRequestDto;
import com.choi.doit.domain.mypage.dto.request.EditPasswordRequestDto;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import com.choi.doit.global.util.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Transactional
class MyPageApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    UserEntity user;
    String accessToken;

    final String EMAIL = "abc@abc.com";
    final String NEW_EMAIL = "def@abc.com";
    final String PASSWORD = "password1234";
    final String NEW_PASSWORD = "password5678";
    final String NICKNAME = "user01";
    final String BASE_URL = "/api/v1/my-page";

    @BeforeEach
    void addMockUser() {
        user = new EmailJoinRequestDto(EMAIL, PASSWORD, NICKNAME).toEntity();
        userRepository.save(user);

        accessToken = "Bearer " + jwtUtil.generateTokens(user).getAccessToken();
    }

    @Test
    @DisplayName("계정 삭제")
    void deleteUser() throws Exception {
        // given
        String url = BASE_URL + "/user";

        // when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
        assertThat(userRepository.findById(user.getId()).orElse(null)).as("계정 삭제가 처리되지 않음.").isNull();
    }

    @Test
    @DisplayName("프로필 이미지 변경")
    void editProfileImage() throws Exception {
        // given
        String url = BASE_URL + "/profile-image";
        String filePath = "src/test/resources/car.jpeg";
        MockMultipartFile mockMultipartFile = new MockMultipartFile("profile", filePath, "image/jpeg", new FileInputStream(filePath));

        // when
        ResultActions resultActions = mockMvc.perform(multipart(HttpMethod.PATCH, url)
                .file(mockMultipartFile)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andDo(print());
        UserEntity userEntity = userRepository.findById(user.getId()).orElseThrow(() -> new Exception("Null"));
        assertThat(userEntity.getProfile_image_path()).as("프로필 사진이 등록되지 않음.").isNotNull();
    }

    @Test
    @DisplayName("이메일 수정")
    void editEmail() throws Exception {
        // given
        String url = BASE_URL + "/email";
        EditEmailRequestDto dto = new EditEmailRequestDto(NEW_EMAIL);

        // when
        ResultActions resultActions = mockMvc.perform(patch(url)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").value(NEW_EMAIL))
                .andDo(print());
        UserEntity userEntity = userRepository.findById(user.getId()).orElseThrow(() -> new Exception("Null"));
        assertThat(userEntity.getEmail()).as("이메일이 변경되지 않음.").isEqualTo(NEW_EMAIL);
    }

    @Test
    @DisplayName("비밀번호 수정")
    void editPassword() throws Exception {
        // given
        String url = BASE_URL + "/password";
        EditPasswordRequestDto dto = new EditPasswordRequestDto(NEW_PASSWORD);

        // when
        ResultActions resultActions = mockMvc.perform(patch(url)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andDo(print());
    }
}
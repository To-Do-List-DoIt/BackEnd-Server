package com.choi.doit.domain.user.api;

import com.choi.doit.domain.user.application.EmailJoinService;
import com.choi.doit.domain.user.application.LoginService;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import com.choi.doit.domain.user.dto.request.EmailLoginRequestDto;
import com.choi.doit.domain.user.dto.response.GuestLoginResponseDto;
import com.choi.doit.domain.user.dto.response.LoginResponseDto;
import com.choi.doit.global.util.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("로그인 API")
@AutoConfigureMockMvc
@Transactional
@Slf4j
class LoginApiTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EmailJoinService emailJoinService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    final String BASE_URL = "/api/v1/user";
    final String EMAIL = "nsuy.ch@gmail.com";
    final String PASSWORD = "password12345";
    final String NICKNAME = "DOIT1";

    // 더미 유저 데이터 생성
    private LoginResponseDto addMockData() {
        // Set up security
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        UserEntity user = userRepository.save(new EmailJoinRequestDto(EMAIL, PASSWORD, NICKNAME).toEntity());

        return jwtUtil.generateTokens(user);
    }

    @Test
    @DisplayName("게스트 이메일 가입")
    void guestJoin() throws Exception {
        // given
        String url = BASE_URL + "/guest/email";
        GuestLoginResponseDto dto = loginService.guestLogin(null, null);
        EmailJoinRequestDto joinRequestDto = new EmailJoinRequestDto(EMAIL, PASSWORD, NICKNAME);
        emailJoinService.saveMailAuthInfo(EMAIL, "test", true);

        // when
        ResultActions resultActions = mockMvc.perform(post(url)
                .header("Authorization", "Bearer " + dto.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(joinRequestDto)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("이메일 로그인 테스트")
    void emailLoginTest() throws Exception {
        // given
        EmailLoginRequestDto dto = new EmailLoginRequestDto(EMAIL, PASSWORD);
        emailJoinService.saveMailAuthInfo(EMAIL, "test", true);
        emailJoinService.join(null, new EmailJoinRequestDto(EMAIL, PASSWORD, NICKNAME));
        String url = BASE_URL + "/login/email";

        // when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto))
        );

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId").exists())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("게스트 로그인 테스트")
    void guestLoginTest() throws Exception {
        // given
        String url = BASE_URL + "/guest";

        // when
        ResultActions resultActions = mockMvc.perform(post(url));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId").exists())
                .andExpect(jsonPath("$.data.userCode").exists())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    @WithMockUser(username = EMAIL)
    void LogoutTest() throws Exception {
        // given
        String bearerToken = "Bearer " + addMockData().getAccessToken();
        String url = BASE_URL + "/logout";

        // when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .header("Authorization", bearerToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("AccessToken 재발급 테스트")
    @WithMockUser(username = EMAIL)
    void RefreshTokenTest() throws Exception {
        // given
        LoginResponseDto dto = addMockData();
        String url = BASE_URL + "/token";

        // when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .header("Authorization", "Bearer " + dto.getAccessToken())
                .header("Authorization-refresh", "Bearer " + dto.getRefreshToken()));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andDo(print());
    }
}
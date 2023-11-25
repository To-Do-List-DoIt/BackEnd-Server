package com.choi.doit.domain.user.api;

import com.choi.doit.domain.user.application.EmailJoinService;
import com.choi.doit.domain.user.dto.request.EmailAuthChangeRequestDto;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import com.choi.doit.global.util.RandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("회원가입 API")
@AutoConfigureMockMvc
@Transactional
@Slf4j
class JoinApiTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private EmailJoinService emailJoinService;

    @Autowired
    private RandomUtil randomUtil;

    final String BASE_URL = "/api/v1/user/sign-up";
    final String EMAIL = "nsuy.ch@gmail.com";
    final String PASSWORD = "password12345";
    final String NICKNAME = "나는천재";

    // 더미 이메일 인증 요청 데이터 등록
    private String addMockEmailRequest(Boolean isAuth) {
        String code = randomUtil.getRandomCode(8);
        emailJoinService.saveMailAuthInfo(EMAIL, code, isAuth);

        return code;
    }

    @Test
    @DisplayName("이메일 인증 링크 요청")
    void postEmailForLink() throws Exception {
        // given
        String url = BASE_URL + "/email/link";

        // when
        ResultActions resultActions = mockMvc.perform(post(url)
                .param("email", EMAIL));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("이메일 인증 링크 변경 요청")
    void patchEmailForLink() throws Exception {
        // given
        String code = addMockEmailRequest(false);
        String url = BASE_URL + "/email/link";
        EmailAuthChangeRequestDto dto = new EmailAuthChangeRequestDto(EMAIL, code, "chys.biz@gmail.com");

        // when
        ResultActions resultActions = mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("이메일 인증 요청 - 유저가 이메일 링크 클릭한 경우")
    void emailConfirm() throws Exception {
        // given
        String code = addMockEmailRequest(false);
        String url = BASE_URL + "/email/link-confirm";

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .param("email", EMAIL)
                .param("code", code));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("이메일 인증 여부 확인")
    void checkAuthInfo() throws Exception {
        // given
        addMockEmailRequest(false);
        String url = BASE_URL + "/email/link";

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .param("email", EMAIL));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(EMAIL))
                .andExpect(jsonPath("$.data.authorized").isBoolean())
                .andDo(print());
    }

    @Test
    @DisplayName("이메일 중복확인")
    void checkDuplicate_email() throws Exception {
        // given
        String testEmail = "abc56@abc.com";
        String url = BASE_URL + "/check";
        String type = "email";

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .param("type", type)
                .param("value", testEmail));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("닉네임 중복확인")
    void checkDuplicate_nickname() throws Exception {
        // given
        String testNickname = "test_닉네임";
        String url = BASE_URL + "/check";
        String type = "nickname";

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .param("type", type)
                .param("value", testNickname));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("이메일 가입")
    void join() throws Exception {
        // given
        addMockEmailRequest(true);
        String url = BASE_URL + "/email";
        EmailJoinRequestDto dto = new EmailJoinRequestDto(EMAIL, PASSWORD, NICKNAME);

        // when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId").exists())
                .andDo(print());
    }
}
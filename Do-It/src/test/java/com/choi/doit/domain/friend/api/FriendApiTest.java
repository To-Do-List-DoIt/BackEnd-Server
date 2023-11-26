package com.choi.doit.domain.friend.api;

import com.choi.doit.domain.friend.dao.FriendRepository;
import com.choi.doit.domain.friend.dao.FriendRequestRepository;
import com.choi.doit.domain.friend.domain.FriendEntity;
import com.choi.doit.domain.friend.domain.FriendRequestEntity;
import com.choi.doit.domain.friend.dto.FriendRequestDto;
import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dao.TodoRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Transactional
class FriendApiTest {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mvc;

    UserEntity user;
    UserEntity friend;
    String accessToken;

    final String BASE_URL = "/api/v1/friend";
    final String EMAIL = "abc@abc.com";
    final String FRIEND_EMAIL = "def@abc.com";
    final String PASSWORD = "password1234";
    final String NICKNAME = "user01";
    final String FRIEND_NICKNAME = "user02";

    @BeforeEach
    void addMockUser() {
        user = new EmailJoinRequestDto(EMAIL, PASSWORD, NICKNAME).toEntity();
        friend = new EmailJoinRequestDto(FRIEND_EMAIL, PASSWORD, FRIEND_NICKNAME).toEntity();
        userRepository.save(user);
        userRepository.save(friend);

        accessToken = "Bearer " + jwtUtil.generateTokens(user).getAccessToken();
    }

    @Test
    @DisplayName("친구 신청")
    void requestFriend() throws Exception {
        // given
        String url = BASE_URL + "/request";
        FriendRequestDto dto = new FriendRequestDto(FRIEND_EMAIL);

        // when
        ResultActions resultActions = mvc.perform(post(url)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("친구 수락")
    void acceptFriend() throws Exception {
        // given
        friendRequestRepository.save(new FriendRequestEntity(friend, user));
        FriendRequestDto dto = new FriendRequestDto(FRIEND_EMAIL);

        // when
        ResultActions resultActions = mvc.perform(post(BASE_URL)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("친구 신청 송신 내역 조회")
    void getSentRequestList() throws Exception {
        // given
        friendRequestRepository.save(new FriendRequestEntity(user, friend));
        String url = BASE_URL + "/sent-request";

        // when
        ResultActions resultActions = mvc.perform(get(url)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].email").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("친구 신청 수신 내역 조회")
    void getReceivedRequestList() throws Exception {
        // given
        friendRequestRepository.save(new FriendRequestEntity(friend, user));
        String url = BASE_URL + "/received-request";

        // when
        ResultActions resultActions = mvc.perform(get(url)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].email").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("친구 내역 조회")
    void getList() throws Exception {
        // given
        ArrayList<FriendEntity> friendEntities = new ArrayList<>(Arrays.asList(new FriendEntity(user, friend), new FriendEntity(friend, user)));
        friendRepository.saveAll(friendEntities);

        // when
        ResultActions resultActions = mvc.perform(get(BASE_URL)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].email").exists())
                .andDo(print());
    }
}
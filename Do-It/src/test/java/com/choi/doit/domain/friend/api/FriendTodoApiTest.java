package com.choi.doit.domain.friend.api;

import com.choi.doit.domain.friend.dao.FriendRepository;
import com.choi.doit.domain.friend.domain.FriendEntity;
import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dao.TodoRepository;
import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.domain.TodoEntity;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import com.choi.doit.global.util.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Transactional
class FriendTodoApiTest {
    @Autowired
    private FriendRepository friendRepository;

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
    final String CATEGORY_STR = "공부";
    final String DATE_STR = "2023-08-16";
    final String DATE_STR2 = "2023-08-26";
    final String TIME_STR = "08:00";
    final String CONTENT = "content_test";

    @BeforeEach
    void addMockUser() {
        user = new EmailJoinRequestDto(EMAIL, PASSWORD, NICKNAME).toEntity();
        friend = new EmailJoinRequestDto(FRIEND_EMAIL, PASSWORD, FRIEND_NICKNAME).toEntity();
        userRepository.save(user);
        userRepository.save(friend);

        accessToken = "Bearer " + jwtUtil.generateTokens(user).getAccessToken();

        // 친구 데이터 생성
        ArrayList<FriendEntity> friendEntities = new ArrayList<>(Arrays.asList(new FriendEntity(user, friend), new FriendEntity(friend, user)));
        friendRepository.saveAll(friendEntities);

        // 일정 데이터 생성
        CategoryEntity category = new CategoryEntity(friend, CATEGORY_STR, "#FF0000");
        CategoryEntity category2 = new CategoryEntity(friend, "일상", "#FFFF00");

        categoryRepository.save(category);
        categoryRepository.save(category2);

        todoRepository.save(new TodoEntity(friend, CONTENT, category, LocalDate.parse(DATE_STR), LocalTime.parse(TIME_STR)));
        todoRepository.save(new TodoEntity(friend, CONTENT + "2", category, LocalDate.parse(DATE_STR), null));
        todoRepository.save(new TodoEntity(friend, CONTENT + "3", category2, LocalDate.parse(DATE_STR), LocalTime.parse(TIME_STR)));
        todoRepository.save(new TodoEntity(friend, CONTENT, category, LocalDate.parse(DATE_STR2), LocalTime.parse(TIME_STR)));
        todoRepository.save(new TodoEntity(friend, CONTENT + "3", category2, LocalDate.parse(DATE_STR2), LocalTime.parse(TIME_STR)));
    }

    @Test
    @DisplayName("<친구> 날짜(하루) 기준 일정 조회")
    void getDay() throws Exception {
        // given
        String url = BASE_URL + "/day-all";

        // when
        ResultActions resultActions = mvc.perform(get(url)
                .header("Authorization", accessToken)
                .param("email", FRIEND_EMAIL)
                .param("date", DATE_STR));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("<친구> 카테고리 & 날짜(하루) 기준 일정 조회")
    void getDayCategory() throws Exception {
        // given
        String url = BASE_URL + "/day";

        // when
        ResultActions resultActions = mvc.perform(get(url)
                .header("Authorization", accessToken)
                .param("email", FRIEND_EMAIL)
                .param("date", DATE_STR)
                .param("category", CATEGORY_STR));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("<친구> 월별 전체 일정 개수 조회")
    void getMonthCount() throws Exception {
        // given
        String url = BASE_URL + "/month";

        // when
        ResultActions resultActions = mvc.perform(get(url)
                .header("Authorization", accessToken)
                .param("email", FRIEND_EMAIL)
                .param("year-month", "2023-08"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").exists())
                .andExpect(jsonPath("$.data.result[0].count").value(3))
                .andDo(print());
    }
}
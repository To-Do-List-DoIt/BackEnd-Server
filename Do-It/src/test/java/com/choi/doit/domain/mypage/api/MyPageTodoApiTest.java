package com.choi.doit.domain.mypage.api;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Transactional
class MyPageTodoApiTest {
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
    String accessToken;

    final String BASE_URL = "/api/v1/my-page/todo";
    final String EMAIL = "abc@abc.com";
    final String PASSWORD = "password1234";
    final String NICKNAME = "user01";
    final String CATEGORY_STR = "공부";
    final String DATE_STR = "2023-08-16";
    final String DATE_STR2 = "2023-08-26";
    final String TIME_STR = "08:00";
    final String CONTENT = "content_test";

    @BeforeEach
    void addMockUser() {
        user = new EmailJoinRequestDto(EMAIL, PASSWORD, NICKNAME).toEntity();
        userRepository.save(user);

        accessToken = "Bearer " + jwtUtil.generateTokens(user).getAccessToken();

        // 일정 데이터 생성
        CategoryEntity category = new CategoryEntity(user, CATEGORY_STR, "#FF0000");
        CategoryEntity category2 = new CategoryEntity(user, "일상", "#FFFF00");

        categoryRepository.save(category);
        categoryRepository.save(category2);

        todoRepository.save(new TodoEntity(user, CONTENT, category, LocalDate.parse(DATE_STR), LocalTime.parse(TIME_STR)));
        todoRepository.save(new TodoEntity(user, CONTENT + "2", category, LocalDate.parse(DATE_STR), null)).setCheckStatus(true);
        todoRepository.save(new TodoEntity(user, CONTENT + "3", category2, LocalDate.parse(DATE_STR), LocalTime.parse(TIME_STR)));
        todoRepository.save(new TodoEntity(user, CONTENT, category, LocalDate.parse(DATE_STR2), LocalTime.parse(TIME_STR))).setCheckStatus(true);
        todoRepository.save(new TodoEntity(user, CONTENT + "3", category2, LocalDate.parse(DATE_STR2), LocalTime.parse(TIME_STR)));
    }

    @Test
    @DisplayName("미완료 투두 존재 여부 조회")
    void hasUnfinishedTodo() throws Exception {
        // given
        String url = BASE_URL + "/unfinished";

        // when
        ResultActions resultActions = mvc.perform(head(url)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isBoolean())
                .andDo(print());
    }

    @Test
    @DisplayName("미완료 투두 리스트 조회")
    void readUnfinishedTodoList() throws Exception {
        // given
        String url = BASE_URL + "/unfinished";

        // when
        ResultActions resultActions = mvc.perform(get(url)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("완료 투두 개수 조회")
    void countFinishedTodo() throws Exception {
        // given
        String url = BASE_URL + "/finished/count";

        // when
        ResultActions resultActions = mvc.perform(get(url)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("완료 투두 상위 2 건 조회")
    void readFinishedTodoTop2() throws Exception {
        // given
        String url = BASE_URL + "/finished/top2";

        // when
        ResultActions resultActions = mvc.perform(get(url)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list").exists())
                .andDo(print());
    }
}
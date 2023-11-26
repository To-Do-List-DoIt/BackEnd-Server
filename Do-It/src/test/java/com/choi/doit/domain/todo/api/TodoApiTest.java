package com.choi.doit.domain.todo.api;

import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dao.TodoRepository;
import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.domain.TodoEntity;
import com.choi.doit.domain.todo.dto.request.EditTodoRequestDto;
import com.choi.doit.domain.todo.dto.request.NewTodoRequestDto;
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

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Transactional
class TodoApiTest {
    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    private MockMvc mvc;
    final String AUTHORIZATION = "Authorization";
    final String PARAM_DATE = "date";
    final String EMAIL = "abc@abc.com";
    final String PASSWORD = "password1234";
    final String NICKNAME = "user01";
    final String CATEGORY_STR = "공부";
    final String DATE_STR = "2023-08-16";
    final String DATE_STR2 = "2023-08-26";
    final String TIME_STR = "08:00";
    final String CONTENT = "content_test";
    final String BASE_URL = "/api/v1/todo";
    UserEntity user;
    String accessToken;

    @Autowired
    TodoApiTest(CategoryRepository categoryRepository, TodoRepository todoRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.categoryRepository = categoryRepository;
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @BeforeEach
    void addMockUser() {
        user = new EmailJoinRequestDto(EMAIL, PASSWORD, NICKNAME).toEntity();
        userRepository.save(user);

        accessToken = "Bearer " + jwtUtil.generateTokens(user).getAccessToken();
    }

    void addMockData() {
        CategoryEntity category = new CategoryEntity(user, CATEGORY_STR, "#FF0000");
        CategoryEntity category2 = new CategoryEntity(user, "일상", "#FFFF00");
        categoryRepository.save(category);
        categoryRepository.save(category2);

        LocalDate date = LocalDate.parse(DATE_STR);
        LocalDate date2 = LocalDate.parse(DATE_STR2);
        LocalTime time = LocalTime.parse(TIME_STR);

        todoRepository.save(new TodoEntity(user, CONTENT, category, date, time));
        todoRepository.save(new TodoEntity(user, CONTENT + "2", category, date, null));
        todoRepository.save(new TodoEntity(user, CONTENT + "3", category2, date, time));
        todoRepository.save(new TodoEntity(user, CONTENT, category, date2, time));
        todoRepository.save(new TodoEntity(user, CONTENT + "3", category2, date2, time));
    }

    @Test
    @DisplayName("일정 추가")
    void addNew() throws Exception {
        // given
        CategoryEntity category = new CategoryEntity(user, CATEGORY_STR, "#FF0000");
        categoryRepository.save(category);
        NewTodoRequestDto dto = new NewTodoRequestDto(CONTENT, CATEGORY_STR, DATE_STR, TIME_STR);

        // when
        ResultActions resultActions = mvc.perform(post(BASE_URL)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.todoId").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("일정 삭제")
    void deleteTodo() throws Exception {
        // given
        CategoryEntity category = new CategoryEntity(user, CATEGORY_STR, "#FF0000");
        categoryRepository.save(category);
        TodoEntity todo = todoRepository.save(new TodoEntity(user, CONTENT, category, LocalDate.parse(DATE_STR), LocalTime.parse(TIME_STR)));
        String url = BASE_URL + "/" + todo.getId();

        // when
        ResultActions resultActions = mvc.perform(delete(url)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
        assertThat(todoRepository.findById(todo.getId()).orElse(null)).as("데이터가 삭제되지 않음.").isNull();
    }

    @Test
    @DisplayName("일정 수정")
    void edit() throws Exception {
        // given
        CategoryEntity category = new CategoryEntity(user, CATEGORY_STR, "#FF0000");
        categoryRepository.save(category);
        TodoEntity todo = todoRepository.save(new TodoEntity(user, CONTENT, category, LocalDate.parse(DATE_STR), LocalTime.parse(TIME_STR)));
        String content = "수정한 내용";
        EditTodoRequestDto dto = new EditTodoRequestDto(content, CATEGORY_STR, DATE_STR, null);
        String url = BASE_URL + "/" + todo.getId();

        // when
        ResultActions resultActions = mvc.perform(patch(url)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andDo(print());
        TodoEntity todoEntity = todoRepository.findById(todo.getId()).orElseThrow(() -> new Exception("Null"));
        assertThat(todoEntity.getContent()).as("데이터가 수정되지 않음").isEqualTo(content);
        assertThat(todoEntity.getTime()).as("데이터가 수정되지 않음").isNull();
    }

    @Test
    @DisplayName("일정 달성 설정")
    void setCheck() throws Exception {
        // given
        CategoryEntity category = new CategoryEntity(user, CATEGORY_STR, "#FF0000");
        categoryRepository.save(category);
        TodoEntity todo = todoRepository.save(new TodoEntity(user, CONTENT, category, LocalDate.parse(DATE_STR), LocalTime.parse(TIME_STR)));
        String url = BASE_URL + "/check/" + todo.getId();

        // when
        ResultActions resultActions = mvc.perform(patch(url)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andDo(print());
        TodoEntity todoEntity = todoRepository.findById(todo.getId()).orElseThrow(() -> new Exception("Null"));
        assertThat(todoEntity.getCheckStatus()).as("데이터가 수정되지 않음").isTrue();
    }

    @Test
    @DisplayName("날짜(하루) 기준 일정 조회")
    void getDay() throws Exception {
        // given
        addMockData();
        String url = BASE_URL + "/day-all";

        // when
        ResultActions resultActions = mvc.perform(get(url)
                .header(AUTHORIZATION, accessToken)
                .param(PARAM_DATE, DATE_STR));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리&날짜(하루) 기준 일정 조회")
    void getDayCategory() throws Exception {
        // given
        addMockData();
        String url = BASE_URL + "/day";

        // when
        ResultActions resultActions = mvc.perform(get(url)
                .header(AUTHORIZATION, accessToken)
                .param(PARAM_DATE, DATE_STR)
                .param("category", CATEGORY_STR));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("월별 전체 일정 개수 조회")
    void getMonthCount() throws Exception {
        // given
        addMockData();
        String url = BASE_URL + "/month";

        // when, then
        mvc.perform(get(url)
                        .header(AUTHORIZATION, accessToken)
                        .param("year-month", "2023-08"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").exists())
                .andExpect(jsonPath("$.data.result[0].count").value(2))
                .andDo(print());
    }
}
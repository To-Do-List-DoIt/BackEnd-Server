package com.choi.doit.domain.todo.api;

import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dao.TodoRepository;
import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.domain.TodoEntity;
import com.choi.doit.domain.todo.dto.CategoryListItemDto;
import com.choi.doit.domain.todo.dto.request.AddCategoryRequestDto;
import com.choi.doit.domain.todo.dto.request.EditCategoryRequestDto;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Transactional
public class TodoCategoryApiTest {
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

    final String EMAIL = "abc@abc.com";
    final String PASSWORD = "password1234";
    final String NICKNAME = "user01";
    final String CATEGORY_STR = "공부";
    final String CATEGORY_STR2 = "일상";
    final String DATE_STR = "2023-08-16";
    final String TIME_STR = "08:00:00";
    final String CONTENT = "content_test";
    final String BASE_URL = "/api/v1/todo/category";

    UserEntity user;
    String accessToken;

    @BeforeEach
    void addTodoData() {
        user = new EmailJoinRequestDto(EMAIL, PASSWORD, NICKNAME).toEntity();
        userRepository.save(user);

        String token = jwtUtil.generateTokens(user).getAccessToken();
        accessToken = "Bearer " + token;

        CategoryEntity category = new CategoryEntity(user, CATEGORY_STR, "#FF0000");
        CategoryEntity category2 = new CategoryEntity(user, CATEGORY_STR2, "#FFFF00");
        categoryRepository.save(category);
        categoryRepository.save(category2);

        LocalDate date = LocalDate.parse(DATE_STR);
        LocalTime time = LocalTime.parse(TIME_STR);

        todoRepository.save(new TodoEntity(user, CONTENT, category, date, time));
        todoRepository.save(new TodoEntity(user, CONTENT + "3", category2, date, time));
    }

    @Test
    @DisplayName("전체 카테고리 조회")
    void getAllCategory() throws Exception {
        // given, when
        ResultActions resultActions = mvc.perform(get(BASE_URL)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리 추가")
    void addNewCategory() throws Exception {
        // given
        AddCategoryRequestDto dto = new AddCategoryRequestDto("프로젝트", "#341414", true);

        // when
        ResultActions resultActions = mvc.perform(post(BASE_URL)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andDo(print());
        ArrayList<CategoryListItemDto> result = categoryRepository.findAllByUserWithJpql(user);
        assertThat(result.size()).as("카테고리 개수가 올바르지 않음.").isEqualTo(3);
        assertThat(result.get(2).getName()).as("카테고리명이 올바르지 않음.").isEqualTo("프로젝트");
    }

    @Test
    @DisplayName("카테고리 수정")
    void editCategory() throws Exception {
        // given
        Long categoryId = categoryRepository.save(new CategoryEntity(user, "프로젝트", "#341414", true)).getId();
        EditCategoryRequestDto dto = new EditCategoryRequestDto("프로젝트에서변경", "#341313", true);
        String url = BASE_URL + "/" + categoryId;

        // when
        ResultActions resultActions = mvc.perform(patch(url)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andDo(print());
        ArrayList<CategoryListItemDto> result = categoryRepository.findAllByUserWithJpql(user);
        assertThat(result.size()).as("카테고리 개수가 올바르지 않음.").isEqualTo(3);
        assertThat(result.get(2).getName()).as("카테고리명이 올바르지 않음.").isEqualTo("프로젝트에서변경");
    }

    @Test
    @DisplayName("카테고리 삭제")
    void removeCategory() throws Exception {
        // given
        LocalDate date = LocalDate.parse(DATE_STR);
        LocalTime time = LocalTime.parse(TIME_STR);
        CategoryEntity category = categoryRepository.save(new CategoryEntity(user, "프로젝트", "#341414", true));
        todoRepository.save(new TodoEntity(user, CONTENT, category, date, time));
        todoRepository.save(new TodoEntity(user, CONTENT + "3", category, date, time));
        String url = BASE_URL + "/" + category.getId();

        // when
        ResultActions resultActions = mvc.perform(delete(url)
                .header("Authorization", accessToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
        List<TodoEntity> result = todoRepository.findAllByUser(user);
        assertThat(result.get(1).getCategory()).as("카테고리 데이터가 올바르지 않음.").isNotNull();
        assertThat(result.get(3).getCategory()).as("카테고리 데이터가 삭제되지 않음.").isNull();
    }
}

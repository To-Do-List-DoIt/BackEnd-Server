package com.choi.doit.domain.todo.api;

import com.choi.doit.domain.model.CategoryEntity;
import com.choi.doit.domain.model.TodoEntity;
import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dao.TodoRepository;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import com.choi.doit.global.util.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class TodoApiTest {
    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    private MockMvc mvc;
    final String AUTHORIZATION = "Authorization";
    final String PARAM_DATE = "date";
    String accessToken = "Bearer ";
    final String email = "abc@abc.com";
    final String password = "password1234";
    final String nickname = "user01";
    final String categoryStr = "Study";
    final String dateStr = "2023-08-26";
    final String timeStr = "08:00:00";
    final String content = "content_test";

    @Autowired
    TodoApiTest(CategoryRepository categoryRepository, TodoRepository todoRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.categoryRepository = categoryRepository;
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @BeforeEach
    void addTodoData() {
        UserEntity user = new UserEntity(new EmailJoinRequestDto(email, password, null), null);
        userRepository.save(user);

        String token = jwtUtil.generateTokens(user).getAccess_token();
        accessToken += token;

        CategoryEntity category = new CategoryEntity(user, categoryStr, "FF0000");
        categoryRepository.save(category);

        LocalDate date = LocalDate.parse(dateStr);
        LocalTime time = LocalTime.parse(timeStr);

        TodoEntity todo = new TodoEntity(user, content, category, date, time);
        todoRepository.save(todo);
    }

    @Test
    void getMonthCount() throws Exception {
        mvc.perform(get("/todo/month")
                        .header(AUTHORIZATION, accessToken)
                        .param(PARAM_DATE, dateStr))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data.result").exists())
                .andExpect(jsonPath("$.data.result[0].count").value(1))
                .andDo(print());
    }

    @AfterEach
    void removeTodoData() throws Exception {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found."));
        LocalDate date = LocalDate.parse(dateStr);
        CategoryEntity category = categoryRepository.findByUserAndName(user, categoryStr)
                .orElseThrow(() -> new Exception("Category not found."));
        ArrayList<TodoEntity> list = todoRepository.findAllByUserAndDate(user, date);

        todoRepository.deleteAll(list);
        categoryRepository.delete(category);
        userRepository.delete(user);
    }
}
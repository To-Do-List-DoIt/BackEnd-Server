package com.choi.doit.domain.todo.dao;

import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.domain.TodoEntity;
import com.choi.doit.domain.todo.dto.TodoCountDto;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class TodoRepositoryTest {
    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    final String email = "abc@abc.com";
    final String password = "password1234";
    final String nickname = "user01";
    final String categoryStr = "Study";
    final String dateStr = "2023-08-26";
    final String timeStr = "08:00:00";
    final String content = "content_test";

    @Autowired
    public TodoRepositoryTest(CategoryRepository categoryRepository, TodoRepository todoRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void addTodoData() {
        UserEntity user = new EmailJoinRequestDto(email, password, nickname).toEntity();
        userRepository.save(user);

        CategoryEntity category = new CategoryEntity(user, categoryStr, "FF0000");
        categoryRepository.save(category);

        LocalDate date = LocalDate.parse(dateStr);
        LocalTime time = LocalTime.parse(timeStr);

        TodoEntity todo = new TodoEntity(user, content, category, date, time);
        todoRepository.save(todo);
    }

    @Test
    void findTodoCountDtoJPQLTest() {
        // given
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        LocalDate date = LocalDate.parse(dateStr);
        LocalDate startDate = date.withDayOfMonth(1);
        LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth()); // 해당 달의 마지막 날

        // when
        LinkedList<TodoCountDto> list = todoRepository.findCountByUserAndDateBetweenGroupByDateWithJpql(user, startDate, endDate);

        // then
        assertThat(list.get(0).getCount()).isEqualTo(1);

        for (TodoCountDto dto : list) {
            log.info(dto.getDay() + ": " + dto.getCount());
        }
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
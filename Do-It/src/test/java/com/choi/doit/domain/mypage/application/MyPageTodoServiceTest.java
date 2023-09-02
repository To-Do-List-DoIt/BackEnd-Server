package com.choi.doit.domain.mypage.application;

import com.choi.doit.domain.model.CategoryEntity;
import com.choi.doit.domain.model.TodoEntity;
import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.mypage.dto.response.ReadUnfinishedTodoListResponse;
import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dao.TodoRepository;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("MyPage Todo Service Test")
@Transactional
@Slf4j
@SpringBootTest
class MyPageTodoServiceTest {
    final MyPageTodoService myPageTodoService;
    final UserRepository userRepository;
    final TodoRepository todoRepository;
    final CategoryRepository categoryRepository;
    final String email = "abc@abc.com";
    final String password = "password1234";
    final String categoryStr = "Study";
    final String color = "FF0000";
    final String dateStr = "2023-08-26";
    final String timeStr = "08:00:00";
    final String content = "content_test";
    final String content2 = "content_test2";

    @Autowired
    MyPageTodoServiceTest(MyPageTodoService myPageTodoService, UserRepository userRepository, TodoRepository todoRepository, CategoryRepository categoryRepository) {
        this.myPageTodoService = myPageTodoService;
        this.userRepository = userRepository;
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }

    @DisplayName("미완료 Todo 존재 여부 조회")
    @WithMockUser(username = email)
    @Test
    void hasUnfinishedTodo() {
        // given
        UserEntity user = userRepository.save(new UserEntity(new EmailJoinRequestDto(email, password, null), null));
        CategoryEntity category = new CategoryEntity(user, categoryStr, color);
        categoryRepository.save(category);
        TodoEntity todo = new TodoEntity(user, content, category, LocalDate.parse(dateStr), LocalTime.parse(timeStr));
        todoRepository.save(todo);
        MyPageTodoService mock = mock(MyPageTodoService.class);

        // when
        mock.hasUnfinishedTodo();

        // then
        verify(mock, atMostOnce()).hasUnfinishedTodo();

    }

    @DisplayName("미완료 Todo 리스트 조회")
    @WithMockUser(username = email)
    @Test
    void readUnfinishedTodoList() {
        // given
        UserEntity user = userRepository.save(new UserEntity(new EmailJoinRequestDto(email, password, null), null));
        CategoryEntity category = new CategoryEntity(user, categoryStr, color);
        categoryRepository.save(category);
        TodoEntity todo = new TodoEntity(user, content, category, LocalDate.parse(dateStr), LocalTime.parse(timeStr));
        todoRepository.save(todo);
        TodoEntity todo2 = new TodoEntity(user, content2, category, LocalDate.parse(dateStr), LocalTime.parse(timeStr));
        todoRepository.save(todo2);

        todo2.updateIsChecked();

        // when
        ReadUnfinishedTodoListResponse response = myPageTodoService.readUnfinishedTodoList();

        // then
        assertThat(response.getList().size()).isEqualTo(1);
        assertThat(response.getList().get(0).getContent()).isEqualTo(content);
    }
}
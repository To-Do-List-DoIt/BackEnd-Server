package com.choi.doit.domain.friend.application;

import com.choi.doit.domain.friend.dao.FriendRepository;
import com.choi.doit.domain.friend.domain.FriendEntity;
import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dao.TodoRepository;
import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.domain.TodoEntity;
import com.choi.doit.domain.todo.dto.TodoCountDto;
import com.choi.doit.domain.todo.dto.response.CategoryDayTodoDto;
import com.choi.doit.domain.todo.dto.response.DayTodoDto;
import com.choi.doit.domain.todo.dto.response.MonthCountDto;
import com.choi.doit.domain.todo.dto.response.TodoItemDto;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import com.choi.doit.global.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Friend To-Do API Test")
@Transactional
@Slf4j
@SpringBootTest
class FriendTodoServiceTest {
    final FriendTodoService friendTodoService;
    final UserRepository userRepository;
    final FriendRepository friendRepository;
    final CategoryRepository categoryRepository;
    final TodoRepository todoRepository;
    final DatetimeUtil datetimeUtil;
    UserEntity user;
    final String EMAIL = "abc@abc.com";
    final String PASSWORD = "password1234";
    UserEntity friend1;
    final String FRIEND_EMAIL = "friend1@abc.com";
    final String CATEGORY_KEY = "category_test";
    final String CONTENT = "test test test";

    @Autowired
    FriendTodoServiceTest(FriendTodoService friendTodoService, UserRepository userRepository, FriendRepository friendRepository, CategoryRepository categoryRepository, TodoRepository todoRepository, DatetimeUtil datetimeUtil) {
        this.friendTodoService = friendTodoService;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.categoryRepository = categoryRepository;
        this.todoRepository = todoRepository;
        this.datetimeUtil = datetimeUtil;
    }

    @BeforeEach
    void addMockData() {
        // UserEntity
        user = userRepository.save(new EmailJoinRequestDto(EMAIL, PASSWORD, null).toEntity(null));

        // FriendUserEntity
        friend1 = userRepository.save(new EmailJoinRequestDto(FRIEND_EMAIL, PASSWORD, null).toEntity(null));

        // Set friend
        friendRepository.save(new FriendEntity(user, friend1));
        friendRepository.save(new FriendEntity(friend1, user));
    }

    void addTodoData(UserEntity user, String dateStr, String timeStr) {
        CategoryEntity category = new CategoryEntity(user, CATEGORY_KEY, "FF0000");
        categoryRepository.save(category);

        LocalDate date = datetimeUtil.parseDate(dateStr);
        LocalTime time = datetimeUtil.parseTime(timeStr);

        TodoEntity todo = new TodoEntity(user, CONTENT, category, date, time);
        todoRepository.save(todo);
    }

    @DisplayName("친구 정보 유효성 검사")
    @WithMockUser(username = EMAIL)
    @Test
    void getFriendEntity() {
        // given
        // when
        UserEntity friend = friendTodoService.getFriendEntity(user, FRIEND_EMAIL);

        // then
        assertThat(friend.getEmail()).isEqualTo(FRIEND_EMAIL);
    }

    @DisplayName("친구의 하루 일정 조회")
    @WithMockUser(username = EMAIL)
    @Test
    void readDay() {
        // given
        String dateStr = "2023-09-16";
        String timeStr = "18:00:00";
        addTodoData(friend1, dateStr, timeStr);

        // when
        DayTodoDto dto = friendTodoService.readDay(FRIEND_EMAIL, dateStr);
        Map<String, LinkedList<TodoItemDto>> result = dto.getResult();

        // then
        assertThat(result.get(CATEGORY_KEY).size()).isEqualTo(1);
        assertThat(result.get(CATEGORY_KEY).get(0).getContent()).isEqualTo(CONTENT);
        assertThat(result.get(CATEGORY_KEY).get(0).getDate()).isEqualTo(datetimeUtil.parseDate(dateStr));
        assertThat(result.get(CATEGORY_KEY).get(0).getTime()).isEqualTo(datetimeUtil.parseTime(timeStr));
    }

    @DisplayName("친구의 카테고리 설정한 하루 일정 조회")
    @WithMockUser(username = EMAIL)
    @Test
    void readCategoryDay() {
        // given
        String dateStr = "2023-09-16";
        String timeStr = "18:00:00";
        addTodoData(friend1, dateStr, timeStr);

        // when
        CategoryDayTodoDto dto = friendTodoService.readCategoryDay(FRIEND_EMAIL, dateStr, CATEGORY_KEY);
        LinkedList<TodoItemDto> list = dto.getResult();

        // then
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getCategory()).isEqualTo(CATEGORY_KEY);
        assertThat(list.get(0).getContent()).isEqualTo(CONTENT);
        assertThat(list.get(0).getDate()).isEqualTo(datetimeUtil.parseDate(dateStr));
        assertThat(list.get(0).getTime()).isEqualTo(datetimeUtil.parseTime(timeStr));
    }

    @DisplayName("친구의 월별 전체 일정 개수 조회")
    @WithMockUser(username = EMAIL)
    @Test
    void readMonthCount() {
        // given
        String monthStr = "2023-09";
        String dateStr = "2023-09-16";
        String timeStr = "18:00:00";
        addTodoData(friend1, dateStr, timeStr);
        long day = 16;

        // when
        MonthCountDto dto = friendTodoService.readMonthCount(FRIEND_EMAIL, monthStr);
        LinkedList<TodoCountDto> list = dto.getResult();

        // then
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getCount()).isEqualTo(1);
        assertThat(list.get(0).getDay()).isEqualTo(day);
    }
}
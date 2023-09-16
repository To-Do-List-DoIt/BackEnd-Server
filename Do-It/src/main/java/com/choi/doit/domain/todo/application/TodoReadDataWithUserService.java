package com.choi.doit.domain.todo.application;

import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dao.TodoRepository;
import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.dto.TodoCountDto;
import com.choi.doit.domain.todo.dto.response.CategoryDayTodoDto;
import com.choi.doit.domain.todo.dto.response.DayTodoDto;
import com.choi.doit.domain.todo.dto.response.MonthCountDto;
import com.choi.doit.domain.todo.dto.response.TodoItemDto;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.global.error.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class TodoReadDataWithUserService {
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    // 날짜(하루) 기준 일정 조회
    public DayTodoDto readDay(UserEntity user, LocalDate date) {
        ArrayList<CategoryEntity> categoryList = categoryRepository.findAllByUser(user);
        Map<String, LinkedList<TodoItemDto>> result = new HashMap<>();

        for (CategoryEntity category : categoryList) {
            LinkedList<TodoItemDto> list = todoRepository.findAllByUserAndDateAndCategoryOrderByTimeAscWithJpql(user, date, category);

            result.put(category.getName(), list);
        }

        return new DayTodoDto(result);
    }

    // 카테고리 & 날짜(하루) 기준 일정 조회
    public CategoryDayTodoDto readCategoryDay(UserEntity user, LocalDate date, CategoryEntity category) throws RestApiException {
        LinkedList<TodoItemDto> result = todoRepository.findAllByUserAndDateAndCategoryOrderByTimeAscWithJpql(user, date, category);

        return new CategoryDayTodoDto(result);
    }

    // 월별 전체 일정 개수 조회
    public MonthCountDto readMonthCount(UserEntity user, LocalDate date) throws RestApiException {
        LocalDate startDate = date.withDayOfMonth(1);
        LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth()); // 해당 달의 마지막 날

        LinkedList<TodoCountDto> list = todoRepository.findCountByUserAndDateBetweenGroupByDateWithJpql(user, startDate, endDate);

        return new MonthCountDto(list);
    }
}

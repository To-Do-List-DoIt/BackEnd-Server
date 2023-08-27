package com.choi.doit.domain.todo.application;

import com.choi.doit.domain.model.CategoryEntity;
import com.choi.doit.domain.model.TodoEntity;
import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dao.TodoRepository;
import com.choi.doit.domain.todo.dto.TodoCountDto;
import com.choi.doit.domain.todo.dto.response.CategoryDayTodoDto;
import com.choi.doit.domain.todo.dto.response.DayTodoDto;
import com.choi.doit.domain.todo.dto.response.MonthCountDto;
import com.choi.doit.domain.todo.dto.response.TodoItemDto;
import com.choi.doit.domain.todo.exception.TodoErrorCode;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.DatetimeUtil;
import com.choi.doit.global.util.SecurityContextUtil;
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
public class TodoRService {
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final SecurityContextUtil securityContextUtil;
    private final DatetimeUtil datetimeUtil;

    public DayTodoDto readDay(String date_str) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        LocalDate date = datetimeUtil.parseDate(date_str);

        ArrayList<CategoryEntity> categoryList = categoryRepository.findAllByUser(user);
        Map<String, LinkedList<TodoItemDto>> result = new HashMap<>();

        for (CategoryEntity category : categoryList) {
            LinkedList<TodoEntity> entityList = todoRepository.findAllByUserAndDateAndCategoryOrderByTimeAsc(user, date, category);
            LinkedList<TodoItemDto> dtoList = new LinkedList<>();

            for (TodoEntity todo : entityList) {
                dtoList.add(new TodoItemDto(todo, category));
            }

            result.put(category.getName(), dtoList);
        }

        return new DayTodoDto(result);
    }

    public CategoryDayTodoDto readCategoryDay(String category, String date_str) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        LocalDate date = datetimeUtil.parseDate(date_str);
        CategoryEntity categoryEntity = categoryRepository.findByUserAndName(user, category)
                .orElseThrow(() -> new RestApiException(TodoErrorCode.CATEGORY_NOT_FOUND));

        LinkedList<TodoEntity> find = todoRepository.findAllByUserAndDateAndCategoryOrderByTimeAsc(user, date, categoryEntity);
        LinkedList<TodoItemDto> result = new LinkedList<>();

        for (TodoEntity todo : find) {
            result.add(new TodoItemDto(todo, categoryEntity));
        }

        return new CategoryDayTodoDto(result);
    }

    public MonthCountDto readMonthCount(String date_str) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        LocalDate date = datetimeUtil.parseDate(date_str);

        LocalDate startDate = date.withDayOfMonth(1);
        LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth()); // 해당 달의 마지막 날

        LinkedList<TodoCountDto> list = todoRepository.findCountByUserAndDateBetweenGroupByDateWithJpql(user, startDate, endDate);

        return new MonthCountDto(list);
    }
}

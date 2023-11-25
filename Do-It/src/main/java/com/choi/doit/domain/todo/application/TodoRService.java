package com.choi.doit.domain.todo.application;

import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.dto.response.CategoryDayTodoDto;
import com.choi.doit.domain.todo.dto.response.DayTodoDto;
import com.choi.doit.domain.todo.dto.response.MonthCountDto;
import com.choi.doit.domain.todo.exception.TodoErrorCode;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.DatetimeUtil;
import com.choi.doit.global.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
@Service
public class TodoRService {
    private final CategoryRepository categoryRepository;
    private final SecurityContextUtil securityContextUtil;
    private final DatetimeUtil datetimeUtil;
    private final TodoReadDataWithUserService todoReadDataWithUserService;

    // 하루
    public DayTodoDto readDay(String date_str) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        LocalDate date = datetimeUtil.parseDate(date_str);

        return todoReadDataWithUserService.readDay(user, date);
    }

    // 하루 + 카테고리
    public CategoryDayTodoDto readCategoryDay(String category, String date_str) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        LocalDate date = datetimeUtil.parseDate(date_str);
        CategoryEntity categoryEntity = categoryRepository.findByUserAndName(user, category)
                .orElseThrow(() -> new RestApiException(TodoErrorCode.CATEGORY_NOT_FOUND));

        return todoReadDataWithUserService.readCategoryDay(user, date, categoryEntity);
    }

    // 월별 전체 개수
    public MonthCountDto readMonthCount(String yearMonth) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        LocalDate date = datetimeUtil.parseYearMonth(yearMonth);

        return todoReadDataWithUserService.readMonthCount(user, date);
    }
}

package com.choi.doit.domain.friend.application;

import com.choi.doit.domain.friend.dao.FriendRepository;
import com.choi.doit.domain.friend.exception.FriendErrorCode;
import com.choi.doit.domain.todo.application.TodoReadDataWithUserService;
import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.dto.response.CategoryDayTodoDto;
import com.choi.doit.domain.todo.dto.response.DayTodoDto;
import com.choi.doit.domain.todo.dto.response.MonthCountDto;
import com.choi.doit.domain.todo.exception.TodoErrorCode;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.DatetimeUtil;
import com.choi.doit.global.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FriendTodoService {
    private final SecurityContextUtil securityContextUtil;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final CategoryRepository categoryRepository;
    private final DatetimeUtil datetimeUtil;
    private final TodoReadDataWithUserService todoReadDataWithUserService;

    // 친구 id 유효성 검사
    @Transactional(readOnly = true)
    public UserEntity getFriendEntity(UserEntity user, String email) throws RestApiException {
        UserEntity friend = userRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(FriendErrorCode.TARGET_EMAIL_NOT_FOUND));

        // 친구 여부 검사
        if (!friendRepository.existsByUserAndFriendUser(user, friend))
            throw new RestApiException(FriendErrorCode.FRIEND_NOT_FOUND);

        return friend;
    }

    // 날짜(하루) 기준 일정 조회
    @Transactional(readOnly = true)
    public DayTodoDto readDay(String friendEmail, String date_str) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        LocalDate date = datetimeUtil.parseDate(date_str);

        return todoReadDataWithUserService.readDay(getFriendEntity(user, friendEmail), date);
    }

    // 카테고리 & 날짜(하루) 기준 일정 조회
    @Transactional(readOnly = true)
    public CategoryDayTodoDto readCategoryDay(String friendEmail, String date_str, String category) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        UserEntity friend = getFriendEntity(user, friendEmail);
        LocalDate date = datetimeUtil.parseDate(date_str);
        CategoryEntity categoryEntity = categoryRepository.findByUserAndName(friend, category)
                .orElseThrow(() -> new RestApiException(TodoErrorCode.CATEGORY_NOT_FOUND));

        return todoReadDataWithUserService.readCategoryDay(friend, date, categoryEntity);
    }

    // 월별 전체 일정 개수 조회
    @Transactional(readOnly = true)
    public MonthCountDto readMonthCount(String friendEmail, String date_str) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        LocalDate date = datetimeUtil.parseYearMonth(date_str);

        return todoReadDataWithUserService.readMonthCount(getFriendEntity(user, friendEmail), date);
    }
}

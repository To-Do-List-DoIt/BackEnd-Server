package com.choi.doit.domain.mypage.application;

import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.mypage.dto.TodoListItemWithoutStatusDto;
import com.choi.doit.domain.mypage.dto.response.CountFinishedTodoResponse;
import com.choi.doit.domain.mypage.dto.response.ReadUnfinishedTodoListResponse;
import com.choi.doit.domain.mypage.exception.MyPageErrorCode;
import com.choi.doit.domain.todo.dao.TodoRepository;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class MyPageTodoService {
    private final TodoRepository todoRepository;
    private final SecurityContextUtil securityContextUtil;

    // 미완료 To-Do 존재 여부 조회
    public void hasUnfinishedTodo() throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        Boolean existsUnfinishedTodo = todoRepository.existsByCheckStatusIsFalseAndUser(user);

        if (!existsUnfinishedTodo)
            throw new RestApiException(MyPageErrorCode.UNFINISHED_TODO_NOT_FOUND);
    }

    // 미완료 To-Do 리스트 조회
    public ReadUnfinishedTodoListResponse readUnfinishedTodoList() {
        UserEntity user = securityContextUtil.getUserEntity();
        LinkedList<TodoListItemWithoutStatusDto> list = todoRepository.findAllByUserAndCheckStatusIsFalseWithJpql(user);

        return new ReadUnfinishedTodoListResponse(list);
    }

    // 완료 To-Do 개수 조회
    public CountFinishedTodoResponse countFinishedTodo() {
        UserEntity user = securityContextUtil.getUserEntity();
        Long count = todoRepository.countAllByUserAndCheckStatusIsTrue(user);

        return new CountFinishedTodoResponse(count);
    }
}

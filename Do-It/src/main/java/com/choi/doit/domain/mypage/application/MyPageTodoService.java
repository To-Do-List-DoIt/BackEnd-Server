package com.choi.doit.domain.mypage.application;

import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.mypage.dto.UnfinishedTodoListItemDto;
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
        LinkedList<UnfinishedTodoListItemDto> list = todoRepository.findAllByUserAndCheckStatusIsFalseWithJpql(user);

        return new ReadUnfinishedTodoListResponse(list);
    }
}

package com.choi.doit.domain.mypage.application;

import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.mypage.dto.response.HasUnfinishedTodoResponse;
import com.choi.doit.domain.todo.dao.TodoRepository;
import com.choi.doit.global.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageTodoService {
    private final TodoRepository todoRepository;
    private final SecurityContextUtil securityContextUtil;

    public HasUnfinishedTodoResponse hasUnfinishedTodo() {
        UserEntity user = securityContextUtil.getUserEntity();

        return new HasUnfinishedTodoResponse(todoRepository.existsByCheckStatusIsFalseAndUser(user));
    }
}

package com.choi.doit.domain.mypage.application;

import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.mypage.dto.UnfinishedTodoListItemDto;
import com.choi.doit.domain.mypage.dto.response.HasUnfinishedTodoResponse;
import com.choi.doit.domain.mypage.dto.response.ReadUnfinishedTodoListResponse;
import com.choi.doit.domain.todo.dao.TodoRepository;
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
    public HasUnfinishedTodoResponse hasUnfinishedTodo() {
        UserEntity user = securityContextUtil.getUserEntity();

        return new HasUnfinishedTodoResponse(todoRepository.existsByCheckStatusIsFalseAndUser(user));
    }

    // 미완료 To-Do 리스트 조회
    public ReadUnfinishedTodoListResponse readUnfinishedTodoList() {
        UserEntity user = securityContextUtil.getUserEntity();
        LinkedList<UnfinishedTodoListItemDto> list = todoRepository.findAllByUserAndCheckStatusIsFalseWithJpql(user);

        return new ReadUnfinishedTodoListResponse(list);
    }
}

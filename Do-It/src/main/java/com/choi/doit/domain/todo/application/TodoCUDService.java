package com.choi.doit.domain.todo.application;

import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dao.TodoRepository;
import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.domain.TodoEntity;
import com.choi.doit.domain.todo.dto.request.EditTodoRequestDto;
import com.choi.doit.domain.todo.dto.request.NewTodoRequestDto;
import com.choi.doit.domain.todo.dto.response.CheckResponseDto;
import com.choi.doit.domain.todo.dto.response.NewTodoResponseDto;
import com.choi.doit.domain.todo.exception.TodoErrorCode;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.DatetimeUtil;
import com.choi.doit.global.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class TodoCUDService {
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final SecurityContextUtil securityContextUtil;
    private final DatetimeUtil datetimeUtil;

    @Transactional
    public NewTodoResponseDto addNewTodo(NewTodoRequestDto newTodoRequestDto) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();

        String content = newTodoRequestDto.getContent();
        CategoryEntity category = categoryRepository.findByUserAndName(user, newTodoRequestDto.getCategory())
                .orElseThrow(() -> new RestApiException(TodoErrorCode.CATEGORY_NOT_FOUND));

        LocalDate date = datetimeUtil.parseDate(newTodoRequestDto.getDate());
        LocalTime time = newTodoRequestDto.getTime() == null ? null : datetimeUtil.parseTime(newTodoRequestDto.getTime());

        TodoEntity todoEntity = new TodoEntity(user, content, category, date, time);

        return new NewTodoResponseDto(todoRepository.save(todoEntity).getId());
    }

    @Transactional
    public void deleteTodo(Long todo_id) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        TodoEntity todoEntity = todoRepository.findById(todo_id)
                .orElseThrow(() -> new RestApiException(TodoErrorCode.TODO_NOT_FOUND));

        // 해당 투두 작성자가 아닐 경우
        if (!todoEntity.getUser().equals(user))
            throw new RestApiException(TodoErrorCode.ACCESS_DENIED);

        // 데이터 삭제
        todoRepository.delete(todoEntity);
    }

    @Transactional
    public void editTodo(Long todo_id, EditTodoRequestDto editTodoRequestDto) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        TodoEntity todoEntity = todoRepository.findById(todo_id)
                .orElseThrow(() -> new RestApiException(TodoErrorCode.TODO_NOT_FOUND));
        CategoryEntity category = categoryRepository.findByUserAndName(user, editTodoRequestDto.getCategory())
                .orElseThrow(() -> new RestApiException(TodoErrorCode.CATEGORY_NOT_FOUND));

        // 해당 투두 작성자가 아닐 경우
        if (!todoEntity.getUser().equals(user))
            throw new RestApiException(TodoErrorCode.ACCESS_DENIED);

        LocalDate date = datetimeUtil.parseDate(editTodoRequestDto.getDate());
        LocalTime time = editTodoRequestDto.getTime() == null ? null : datetimeUtil.parseTime(editTodoRequestDto.getTime());

        // 데이터 업데이트
        todoEntity.update(editTodoRequestDto.getContent(), category, date, time);
    }

    @Transactional
    public CheckResponseDto setCheck(Long todo_id) {
        UserEntity user = securityContextUtil.getUserEntity();
        TodoEntity todoEntity = todoRepository.findById(todo_id)
                .orElseThrow(() -> new RestApiException(TodoErrorCode.TODO_NOT_FOUND));

        // 해당 투두 작성자가 아닐 경우
        if (!todoEntity.getUser().equals(user))
            throw new RestApiException(TodoErrorCode.ACCESS_DENIED);

        // 데이터 업데이트
        return new CheckResponseDto(todoEntity.updateIsChecked());
    }
}

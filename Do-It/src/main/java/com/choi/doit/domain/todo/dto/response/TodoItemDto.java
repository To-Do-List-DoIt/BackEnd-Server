package com.choi.doit.domain.todo.dto.response;

import com.choi.doit.domain.todo.domain.TodoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@Setter
public class TodoItemDto {
    private Long todoId;
    private String category;
    private String content;
    private LocalDate date;
    private LocalTime time;
    private Boolean isChecked;

    public TodoItemDto(TodoEntity todo) {
        this.todoId = todo.getId();
        this.category = todo.getCategory().getName();
        this.content = todo.getContent();
        this.date = todo.getDate();
        this.time = todo.getTime();
        this.isChecked = todo.getCheckStatus();
    }
}

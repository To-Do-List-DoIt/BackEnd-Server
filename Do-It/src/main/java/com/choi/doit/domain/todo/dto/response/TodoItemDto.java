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
    private Long todo_id;
    private String category;
    private String content;
    private LocalDate date;
    private LocalTime time;
    private Boolean checked;

    public TodoItemDto(TodoEntity todo) {
        this.todo_id = todo.getId();
        this.category = todo.getCategory().getName();
        this.content = todo.getContent();
        this.date = todo.getDate();
        this.time = todo.getTime();
        this.checked = todo.getCheckStatus();
    }
}

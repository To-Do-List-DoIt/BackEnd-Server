package com.choi.doit.domain.mypage.dto;

import com.choi.doit.domain.todo.domain.TodoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Setter
public class TodoListItemWithoutStatusDto {
    private Long todo_id;
    private String category;
    private String content;
    private LocalDate date;
    private LocalTime time;

    public TodoListItemWithoutStatusDto(TodoEntity todo) {
        this.todo_id = todo.getId();
        this.category = todo.getCategory().getName();
        this.content = todo.getContent();
        this.date = todo.getDate();
        this.time = todo.getTime();
    }
}

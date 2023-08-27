package com.choi.doit.domain.todo.dto.response;

import com.choi.doit.domain.model.CategoryEntity;
import com.choi.doit.domain.model.TodoEntity;
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
    private boolean checked;

    public TodoItemDto(TodoEntity todo, CategoryEntity category) {
        this.todo_id = todo.getId();
        this.category = category.getName();
        this.content = todo.getContent();
        this.date = todo.getDate();
        this.time = todo.getTime();
        this.checked = todo.is_checked();
    }
}

package com.choi.doit.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryDayTodoDto {
    LinkedList<TodoItemDto> result;
}

package com.choi.doit.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;

@AllArgsConstructor
@Getter
public class DayTodoDto {
    LinkedList<TodoItemDto> result;
}

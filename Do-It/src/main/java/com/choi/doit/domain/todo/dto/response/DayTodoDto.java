package com.choi.doit.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Map;

@AllArgsConstructor
@Getter
public class DayTodoDto {
    Map<String, LinkedList<TodoItemDto>> result;
}

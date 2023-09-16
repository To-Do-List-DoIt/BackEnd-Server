package com.choi.doit.domain.todo.dto.response;

import com.choi.doit.domain.todo.dto.TodoCountDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;

@AllArgsConstructor
@Getter
public class MonthCountDto {
    private LinkedList<TodoCountDto> result;
}

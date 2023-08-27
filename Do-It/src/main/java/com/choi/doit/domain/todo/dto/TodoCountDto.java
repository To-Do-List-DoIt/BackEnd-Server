package com.choi.doit.domain.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

@AllArgsConstructor
@Setter
@Getter
public class TodoCountDto {
    private Long day;
    private Long count;

    public TodoCountDto(LocalDate date, Long count) {
        this.day = (long) date.get(ChronoField.DAY_OF_MONTH);
        this.count = count;
    }
}

package com.choi.doit.domain.todo.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TodoCountDtoTest {
    @Test
    public void dtoTest_success() {
        //given
        String dateStr = "2023-08-26";
        LocalDate localDate = LocalDate.parse(dateStr);
        Long count = 2L;

        //when
        TodoCountDto todoCountDto = new TodoCountDto(localDate, count);

        //then
        assertThat(todoCountDto.getDay()).isEqualTo(26);
        assertThat(todoCountDto.getCount()).isEqualTo(count);

    }

}
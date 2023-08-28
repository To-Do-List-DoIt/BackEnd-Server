package com.choi.doit.domain.todo.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EditTodoRequestDto {
    @Size(max = 50)
    private String content;
    private String category;
    @Pattern(regexp = "^\\d{2,4}-\\d{1,2}-\\d{1,2}$")
    private String date;
    @Pattern(regexp = "^\\d{1,2}:\\d{1,2}$")
    private String time;
}
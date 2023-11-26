package com.choi.doit.domain.todo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EditTodoRequestDto {
    @NotNull
    @Size(max = 50)
    private String content;
    private String category;
    @NotNull
    @Pattern(regexp = "^\\d{2,4}-\\d{1,2}-\\d{1,2}$")
    private String date;
    @Pattern(regexp = "^\\d{1,2}:\\d{1,2}$")
    private String time;
}

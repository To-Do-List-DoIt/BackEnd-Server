package com.choi.doit.domain.todo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AddCategoryRequestDto {
    @Size(max = 50)
    private String name;
    @NotNull
    private String color;
    private Boolean is_private;
}

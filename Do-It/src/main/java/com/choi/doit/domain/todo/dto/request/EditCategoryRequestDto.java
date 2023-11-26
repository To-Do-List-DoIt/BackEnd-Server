package com.choi.doit.domain.todo.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EditCategoryRequestDto {
    @Size(max = 50)
    private String name;
    private String color;
    private Boolean isPrivate;
}

package com.choi.doit.domain.todo.dto;

import com.choi.doit.domain.model.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CategoryDetailDto {
    private String name;
    private String color;
    private boolean is_private;

    public CategoryDetailDto(CategoryEntity category) {
        this.name = category.getName();
        this.color = category.getColor();
        this.is_private = category.getIsPrivate();
    }
}

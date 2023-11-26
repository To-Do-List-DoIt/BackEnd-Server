package com.choi.doit.domain.todo.dto;

import com.choi.doit.domain.todo.domain.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CategoryDetailDto {
    private String name;
    private String color;
    private Boolean isPrivate;

    public CategoryDetailDto(CategoryEntity category) {
        this.name = category.getName();
        this.color = category.getColor();
        this.isPrivate = category.getIsPrivate();
    }
}

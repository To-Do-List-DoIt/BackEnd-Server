package com.choi.doit.domain.todo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CategoryListItemDto {
    private String name;
    private String color;
    private Boolean isPrivate;

    public CategoryListItemDto(String name, String color, Boolean isPrivate) {
        this.name = name;
        this.color = color;
        this.isPrivate = isPrivate;
    }
}

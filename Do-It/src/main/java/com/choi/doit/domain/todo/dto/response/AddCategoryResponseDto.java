package com.choi.doit.domain.todo.dto.response;

import com.choi.doit.domain.todo.dto.CategoryDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AddCategoryResponseDto {
    private Long category_id;
    private CategoryDetailDto dto;
}

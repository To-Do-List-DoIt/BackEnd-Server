package com.choi.doit.domain.todo.application;

import com.choi.doit.domain.model.CategoryEntity;
import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dto.CategoryDetailDto;
import com.choi.doit.domain.todo.dto.CategoryListItemDto;
import com.choi.doit.domain.todo.dto.request.AddCategoryRequestDto;
import com.choi.doit.domain.todo.dto.request.EditCategoryRequestDto;
import com.choi.doit.domain.todo.dto.response.AddCategoryResponseDto;
import com.choi.doit.domain.todo.exception.TodoErrorCode;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@Service
public class TodoCategoryService {
    private final CategoryRepository categoryRepository;
    private final SecurityContextUtil securityContextUtil;

    public ArrayList<CategoryListItemDto> readAll() {
        UserEntity user = securityContextUtil.getUserEntity();

        return categoryRepository.findAllByUserWithJpql(user);
    }

    public AddCategoryResponseDto addNew(AddCategoryRequestDto addCategoryRequestDto) {
        UserEntity user = securityContextUtil.getUserEntity();

        String name = addCategoryRequestDto.getName();
        String color = addCategoryRequestDto.getColor();
        Boolean isPrivate = addCategoryRequestDto.getIs_private();

        CategoryEntity categoryEntity = new CategoryEntity(user, name, color, isPrivate);
        CategoryEntity category = categoryRepository.save(categoryEntity);

        return new AddCategoryResponseDto(category.getId(), new CategoryDetailDto(category));
    }

    public CategoryDetailDto modify(Long category_id, EditCategoryRequestDto editCategoryRequestDto) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();

        String name = editCategoryRequestDto.getName();
        String color = editCategoryRequestDto.getColor();
        Boolean isPrivate = editCategoryRequestDto.getIs_private();

        CategoryEntity category = categoryRepository.findById(category_id)
                .orElseThrow(() -> new RestApiException(TodoErrorCode.CATEGORY_NOT_FOUND));

        // 카테고리의 user 일치 여부 검사
        if (!category.getUser().equals(user))
            throw new RestApiException(TodoErrorCode.ACCESS_DENIED);

        // 데이터 업데이트
        category.update(name, color, isPrivate);

        return new CategoryDetailDto(category);
    }
}

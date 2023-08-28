package com.choi.doit.domain.todo.application;

import com.choi.doit.domain.model.CategoryEntity;
import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dto.CategoryListItemDto;
import com.choi.doit.domain.todo.dto.request.AddCategoryRequestDto;
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

    public void addNew(AddCategoryRequestDto addCategoryRequestDto) {
        UserEntity user = securityContextUtil.getUserEntity();

        String name = addCategoryRequestDto.getName();
        String color = addCategoryRequestDto.getColor();
        Boolean isPrivate = addCategoryRequestDto.getIs_private();

        CategoryEntity category = new CategoryEntity(user, name, color, isPrivate);
        categoryRepository.save(category);
    }
}

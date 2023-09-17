package com.choi.doit.domain.todo.application;

import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.dto.CategoryDetailDto;
import com.choi.doit.domain.todo.dto.CategoryListItemDto;
import com.choi.doit.domain.todo.dto.request.AddCategoryRequestDto;
import com.choi.doit.domain.todo.dto.request.EditCategoryRequestDto;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Category Service Test")
@Transactional
@Slf4j
@SpringBootTest
class TodoCategoryServiceTest {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TodoCategoryService todoCategoryService;
    final String email = "abc@abc.com";
    final String password = "password1234";
    final String name = "Study";
    final String color = "FF0000";
    final Boolean is_private = false;

    @Autowired
    TodoCategoryServiceTest(CategoryRepository categoryRepository, UserRepository userRepository, TodoCategoryService todoCategoryService) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.todoCategoryService = todoCategoryService;
    }

    void addData() {
        UserEntity user = new EmailJoinRequestDto(email, password).toEntity();
        CategoryEntity category = new CategoryEntity(user, name, color, is_private);
        categoryRepository.save(category);
    }

    @DisplayName("전체 리스트 조회")
    @WithMockUser(username = email)
    @Test
    void readAll() {
        // given
        addData();

        // when
        ArrayList<CategoryListItemDto> list = todoCategoryService.readAll();

        // then
        assertThat(list.get(0).getName()).isEqualTo(name);
        assertThat(list.get(0).getColor()).isEqualTo(color);
    }

    @DisplayName("새 데이터 저장")
    @WithMockUser(username = email)
    @Test
    void addNew() throws Exception {
        // given
        UserEntity user = new EmailJoinRequestDto(email, password).toEntity();
        AddCategoryRequestDto dto = new AddCategoryRequestDto(name, color, is_private);

        // when
        todoCategoryService.addNew(dto);

        // then
        CategoryEntity category = categoryRepository.findByUserAndName(user, name).orElse(null);
        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(name);
        assertThat(category.getColor()).isEqualTo(color);
        assertThat(category.getIsPrivate()).isEqualTo(is_private);
    }

    @DisplayName("데이터 수정")
    @WithMockUser(username = email)
    @Test
    void modify() throws Exception {
        // given
        UserEntity user = userRepository.save(new EmailJoinRequestDto(email, password).toEntity());
        CategoryEntity category = new CategoryEntity(user, name, color, is_private);
        Long id = categoryRepository.save(category).getId();

        // when
        CategoryDetailDto dto = todoCategoryService.modify(id, new EditCategoryRequestDto(name, color, !is_private));
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new Exception("Category not found."));

        // then
        assertThat(categoryEntity.getName()).isEqualTo(name);
        assertThat(categoryEntity.getColor()).isEqualTo(color);
        assertThat(categoryEntity.getIsPrivate()).isEqualTo(!is_private);

        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getColor()).isEqualTo(color);
        assertThat(dto.is_private()).isEqualTo(!is_private);
    }

    @DisplayName("데이터 삭제")
    @WithMockUser(username = email)
    @Test
    void remove() {
        // given
        UserEntity user = userRepository.save(new EmailJoinRequestDto(email, password).toEntity());
        CategoryEntity category = new CategoryEntity(user, name, color, is_private);
        Long id = categoryRepository.save(category).getId();

        // when
        todoCategoryService.remove(id);
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElse(null);

        // then
        assertThat(categoryEntity).isNull();
    }
}
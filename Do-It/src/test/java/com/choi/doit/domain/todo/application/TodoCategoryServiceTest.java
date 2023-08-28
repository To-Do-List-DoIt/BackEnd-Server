package com.choi.doit.domain.todo.application;

import com.choi.doit.domain.model.CategoryEntity;
import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.dto.CategoryListItemDto;
import com.choi.doit.domain.todo.dto.request.AddCategoryRequestDto;
import com.choi.doit.domain.user.dao.UserRepository;
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
    final String nickname = "user01";
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
        UserEntity user = userRepository.save(new UserEntity(new EmailJoinRequestDto(email, password, nickname, null), null));
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
        UserEntity user = userRepository.save(new UserEntity(new EmailJoinRequestDto(email, password, nickname, null), null));
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
}
package com.choi.doit.domain.todo.dao;

import com.choi.doit.domain.model.CategoryEntity;
import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.todo.dto.CategoryListItemDto;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Slf4j
@SpringBootTest
class CategoryRepositoryTest {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    final String email = "abc@abc.com";
    final String password = "password1234";
    final String nickname = "user01";
    final String categoryStr = "Study";
    final String color = "FF0000";

    @Autowired
    CategoryRepositoryTest(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void addData() {
        UserEntity user = new UserEntity(new EmailJoinRequestDto(email, password, null), null);
        userRepository.save(user);

        CategoryEntity category = new CategoryEntity(user, categoryStr, color);
        categoryRepository.save(category);
    }

    @Test
    void findAllByUserWithJpql() throws Exception {
        // given
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found."));

        // when
        ArrayList<CategoryListItemDto> list = categoryRepository.findAllByUserWithJpql(user);

        // then
        assertThat(list.get(0).getName()).isEqualTo(categoryStr);
        assertThat(list.get(0).getColor()).isEqualTo(color);
    }
}
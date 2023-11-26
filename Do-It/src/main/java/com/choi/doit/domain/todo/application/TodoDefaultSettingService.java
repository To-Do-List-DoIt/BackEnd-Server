package com.choi.doit.domain.todo.application;

import com.choi.doit.domain.todo.dao.CategoryRepository;
import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.user.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TodoDefaultSettingService {
    private final CategoryRepository categoryRepository;

    // 기본 카테고리 추가
    @Transactional
    public void addDefaultCategory(UserEntity user) {
        final String DEFAULT_1_NAME = "공부";
        final String DEFAULT_2_NAME = "일상";
        final String DEFAULT_3_NAME = "운동";
        final String DEFAULT_1_COLOR = "#FF5E38";
        final String DEFAULT_2_COLOR = "#0052FE";
        final String DEFAULT_3_COLOR = "#FFD917";

        categoryRepository.save(new CategoryEntity(user, DEFAULT_1_NAME, DEFAULT_1_COLOR));
        categoryRepository.save(new CategoryEntity(user, DEFAULT_2_NAME, DEFAULT_2_COLOR));
        categoryRepository.save(new CategoryEntity(user, DEFAULT_3_NAME, DEFAULT_3_COLOR));
    }
}

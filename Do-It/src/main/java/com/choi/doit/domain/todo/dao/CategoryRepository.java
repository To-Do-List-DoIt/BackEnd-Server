package com.choi.doit.domain.todo.dao;

import com.choi.doit.domain.model.CategoryEntity;
import com.choi.doit.domain.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByUserAndName(UserEntity user, String name);

    ArrayList<CategoryEntity> findAllByUser(UserEntity user);
}

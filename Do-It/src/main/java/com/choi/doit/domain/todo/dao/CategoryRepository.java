package com.choi.doit.domain.todo.dao;

import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.dto.CategoryListItemDto;
import com.choi.doit.domain.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByUserAndName(UserEntity user, String name);

    @Query(value = "select new com.choi.doit.domain.todo.dto.CategoryListItemDto(c.name, c.color, c.isPrivate) " +
            "from CategoryEntity c " +
            "where c.user = :user")
    ArrayList<CategoryListItemDto> findAllByUserWithJpql(@Param(value = "user") UserEntity user);

    void deleteAllByUser(UserEntity user);
}

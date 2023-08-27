package com.choi.doit.domain.todo.dao;

import com.choi.doit.domain.model.CategoryEntity;
import com.choi.doit.domain.model.TodoEntity;
import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.todo.dto.TodoCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    LinkedList<TodoEntity> findAllByUserAndDateAndCategoryOrderByTimeAsc(UserEntity user, LocalDate date, CategoryEntity category);

    ArrayList<TodoEntity> findAllByUserAndDate(UserEntity user, LocalDate date);

    LinkedList<TodoEntity> findAllByUserAndDateBetweenOrderByDate(UserEntity user, LocalDate startDate, LocalDate endDate);

    @Query(value = "select new com.choi.doit.domain.todo.dto.TodoCountDto(t.date, count(t.date)) "
            + "from Todo t "
            + "where t.user = :user "
            + "and t.date between :startDate and :endDate "
            + "group by t.date")
    LinkedList<TodoCountDto> findCountByUserAndDateBetweenGroupByDateWithJpql(@Param(value = "user") UserEntity user, @Param(value = "startDate") LocalDate startDate, @Param(value = "endDate") LocalDate endDate);
}

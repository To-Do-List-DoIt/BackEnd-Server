package com.choi.doit.domain.todo.dao;

import com.choi.doit.domain.mypage.dto.TodoListItemWithoutStatusDto;
import com.choi.doit.domain.todo.domain.CategoryEntity;
import com.choi.doit.domain.todo.domain.TodoEntity;
import com.choi.doit.domain.todo.dto.TodoCountDto;
import com.choi.doit.domain.todo.dto.response.TodoItemDto;
import com.choi.doit.domain.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    @Query(value = "select new com.choi.doit.domain.todo.dto.response.TodoItemDto(t) " +
            "from Todo t " +
            "where t.user = :user " +
            "and t.date = :date " +
            "order by t.category.id asc, t.time asc")
    LinkedList<TodoItemDto> findAllByUserAndDateOrderByTimeAscWithJpql(@Param(value = "user") UserEntity user, @Param(value = "date") LocalDate date);

    @Query(value = "select new com.choi.doit.domain.todo.dto.response.TodoItemDto(t) " +
            "from Todo t " +
            "where t.user = :user " +
            "and t.date = :date " +
            "and t.category = :category " +
            "order by t.time asc")
    LinkedList<TodoItemDto> findAllByUserAndDateAndCategoryOrderByTimeAscWithJpql(@Param(value = "user") UserEntity user, @Param(value = "date") LocalDate date, @Param(value = "category") CategoryEntity category);

    ArrayList<TodoEntity> findAllByUserAndDate(UserEntity user, LocalDate date);

    LinkedList<TodoEntity> findAllByUserAndDateBetweenOrderByDate(UserEntity user, LocalDate startDate, LocalDate endDate);

    @Query(value = "select new com.choi.doit.domain.todo.dto.TodoCountDto(t.date, count(t.date)) "
            + "from Todo t "
            + "where t.user = :user "
            + "and t.date between :startDate and :endDate "
            + "group by t.date")
    LinkedList<TodoCountDto> findCountByUserAndDateBetweenGroupByDateWithJpql(@Param(value = "user") UserEntity user, @Param(value = "startDate") LocalDate startDate, @Param(value = "endDate") LocalDate endDate);

    Boolean existsByCheckStatusIsFalseAndUser(UserEntity user);

    @Query(value = "select new com.choi.doit.domain.mypage.dto.TodoListItemWithoutStatusDto(t) "
            + "from Todo t "
            + "where t.user = :user "
            + "and t.checkStatus = false ")
    LinkedList<TodoListItemWithoutStatusDto> findAllByUserAndCheckStatusIsFalseWithJpql(UserEntity user);

    Long countAllByUserAndCheckStatusIsTrue(UserEntity user);

    LinkedList<TodoEntity> findTop2ByUserAndCheckStatusIsTrueOrderByDateDesc(UserEntity user);

    @Modifying(clearAutomatically = true)
    @Query(value = "update Todo t " +
            "set t.category = null " +
            "where t.user = :user and t.category = :category")
    void updateAllByUserAndCategory(UserEntity user, CategoryEntity category);

    List<TodoEntity> findAllByUser(UserEntity user);

    void deleteAllByUser(UserEntity user);
}

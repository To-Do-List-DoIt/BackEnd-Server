package com.choi.doit.domain.todo.api;

import com.choi.doit.domain.todo.application.TodoCUDService;
import com.choi.doit.domain.todo.application.TodoCategoryService;
import com.choi.doit.domain.todo.application.TodoRService;
import com.choi.doit.domain.todo.dto.CategoryDetailDto;
import com.choi.doit.domain.todo.dto.CategoryListItemDto;
import com.choi.doit.domain.todo.dto.request.AddCategoryRequestDto;
import com.choi.doit.domain.todo.dto.request.EditCategoryRequestDto;
import com.choi.doit.domain.todo.dto.request.EditTodoRequestDto;
import com.choi.doit.domain.todo.dto.request.NewTodoRequestDto;
import com.choi.doit.domain.todo.dto.response.*;
import com.choi.doit.global.common.response.DataResponseDto;
import com.choi.doit.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/todo")
public class TodoApi {
    private final TodoCUDService todoCUDService;
    private final TodoRService todoRService;
    private final TodoCategoryService todoCategoryService;

    @PostMapping
    public ResponseEntity<ResponseDto> addNew(@RequestBody @Valid NewTodoRequestDto newTodoRequestDto) {
        NewTodoResponseDto newTodoResponseDto = todoCUDService.addNewTodo(newTodoRequestDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(newTodoResponseDto, 201));
    }

    @DeleteMapping("/{todo-id}")
    public ResponseEntity<ResponseDto> delete(@PathVariable(value = "todo-id") Long todo_id) {
        todoCUDService.deleteTodo(todo_id);

        return ResponseEntity.status(201).body(ResponseDto.of(201));
    }

    @PatchMapping("/{todo-id}")
    public ResponseEntity<ResponseDto> edit(@PathVariable(value = "todo-id") Long todo_id, @Valid EditTodoRequestDto editTodoRequestDto) {
        todoCUDService.editTodo(todo_id, editTodoRequestDto);

        return ResponseEntity.status(201).body(ResponseDto.of(201));
    }

    @PatchMapping("/check/{todo-id}")
    public ResponseEntity<ResponseDto> setCheck(@PathVariable(value = "todo-id") Long todo_id) {
        CheckResponseDto checkResponseDto = todoCUDService.setCheck(todo_id);

        return ResponseEntity.status(201).body(DataResponseDto.of(checkResponseDto, 201));
    }

    @GetMapping("/day-all")
    public ResponseEntity<ResponseDto> getDay(@RequestParam @NotNull String date) {
        DayTodoDto dayTodoDto = todoRService.readDay(date);

        return ResponseEntity.ok(DataResponseDto.of(dayTodoDto, 200));
    }

    @GetMapping("/day")
    public ResponseEntity<ResponseDto> getDayCategory(@RequestParam @NotNull String date, @RequestParam @NotNull String category) {
        CategoryDayTodoDto categoryDayTodoDto = todoRService.readCategoryDay(category, date);

        return ResponseEntity.ok(DataResponseDto.of(categoryDayTodoDto, 200));
    }

    @GetMapping("/month")
    public ResponseEntity<ResponseDto> getMonthCount(@RequestParam @NotNull String date) {
        MonthCountDto monthCountDto = todoRService.readMonthCount(date);

        return ResponseEntity.ok(DataResponseDto.of(monthCountDto, 200));
    }

    @GetMapping("/category")
    public ResponseEntity<ResponseDto> getAllCategory() {
        ArrayList<CategoryListItemDto> data = todoCategoryService.readAll();

        return ResponseEntity.ok(DataResponseDto.of(data, 200));
    }

    @PostMapping("/category")
    public ResponseEntity<ResponseDto> addNewCategory(AddCategoryRequestDto addCategoryRequestDto) {
        AddCategoryResponseDto addCategoryResponseDto = todoCategoryService.addNew(addCategoryRequestDto);

        return ResponseEntity.created(URI.create("/category/" + addCategoryResponseDto.getCategory_id())).body(DataResponseDto.of(addCategoryResponseDto.getDetail(), 201));
    }

    @PatchMapping("/category/{category-id}")
    public ResponseEntity<ResponseDto> editCategory(@PathVariable("category-id") Long category_id, EditCategoryRequestDto editCategoryRequestDto) {
        CategoryDetailDto categoryDetailDto = todoCategoryService.modify(category_id, editCategoryRequestDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(categoryDetailDto, 201));
    }

    @DeleteMapping("/category/{category-id}")
    public ResponseEntity<ResponseDto> removeCategory(@PathVariable("category-id") Long category_id) {
        todoCategoryService.remove(category_id);

        return ResponseEntity.ok(ResponseDto.of(200));
    }
}

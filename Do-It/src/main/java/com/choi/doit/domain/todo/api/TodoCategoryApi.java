package com.choi.doit.domain.todo.api;

import com.choi.doit.domain.todo.application.TodoCategoryService;
import com.choi.doit.domain.todo.dto.CategoryDetailDto;
import com.choi.doit.domain.todo.dto.CategoryListItemDto;
import com.choi.doit.domain.todo.dto.request.AddCategoryRequestDto;
import com.choi.doit.domain.todo.dto.request.EditCategoryRequestDto;
import com.choi.doit.domain.todo.dto.response.AddCategoryResponseDto;
import com.choi.doit.global.common.response.DataResponseDto;
import com.choi.doit.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/todo/category")
public class TodoCategoryApi {
    private final TodoCategoryService todoCategoryService;

    @GetMapping
    public ResponseEntity<ResponseDto> getAllCategory() {
        ArrayList<CategoryListItemDto> data = todoCategoryService.readAll();

        return ResponseEntity.ok(DataResponseDto.of(data, 200));
    }

    @PostMapping
    public ResponseEntity<ResponseDto> addNewCategory(@RequestBody @Valid AddCategoryRequestDto addCategoryRequestDto) {
        AddCategoryResponseDto addCategoryResponseDto = todoCategoryService.addNew(addCategoryRequestDto);

        return ResponseEntity.created(URI.create("/category/" + addCategoryResponseDto.getCategoryId())).body(DataResponseDto.of(addCategoryResponseDto.getDetail(), 201));
    }

    @PatchMapping("/{category-id}")
    public ResponseEntity<ResponseDto> editCategory(@PathVariable("category-id") Long category_id, @RequestBody @Valid EditCategoryRequestDto editCategoryRequestDto) {
        CategoryDetailDto categoryDetailDto = todoCategoryService.modify(category_id, editCategoryRequestDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(categoryDetailDto, 201));
    }

    @DeleteMapping("/{category-id}")
    public ResponseEntity<ResponseDto> removeCategory(@PathVariable("category-id") Long category_id) {
        todoCategoryService.remove(category_id);

        return ResponseEntity.ok(ResponseDto.of(200));
    }
}

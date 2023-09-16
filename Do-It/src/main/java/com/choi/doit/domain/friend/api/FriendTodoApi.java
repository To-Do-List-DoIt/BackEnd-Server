package com.choi.doit.domain.friend.api;

import com.choi.doit.domain.friend.application.FriendTodoService;
import com.choi.doit.domain.todo.dto.response.CategoryDayTodoDto;
import com.choi.doit.domain.todo.dto.response.DayTodoDto;
import com.choi.doit.domain.todo.dto.response.MonthCountDto;
import com.choi.doit.global.common.response.DataResponseDto;
import com.choi.doit.global.common.response.ResponseDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/friend")
public class FriendTodoApi {
    private final FriendTodoService friendTodoService;

    // 날짜(하루) 기준 일정 조회
    @GetMapping("/day-all")
    public ResponseEntity<ResponseDto> getDay(@RequestParam @NotNull String email, @RequestParam @NotNull String date) {
        DayTodoDto dayTodoDto = friendTodoService.readDay(email, date);

        return ResponseEntity.ok().body(DataResponseDto.of(dayTodoDto, 200));
    }

    // 카테고리 & 날짜(하루) 기준 일정 조회
    @GetMapping("/day")
    public ResponseEntity<ResponseDto> getDayCategory(@RequestParam @NotNull String email, @RequestParam @NotNull String date, @RequestParam @NotNull String category) {
        CategoryDayTodoDto categoryDayTodoDto = friendTodoService.readCategoryDay(email, category, date);

        return ResponseEntity.ok(DataResponseDto.of(categoryDayTodoDto, 200));
    }

    // 월별 전체 일정 개수 조회
    @GetMapping("/month")
    public ResponseEntity<ResponseDto> getMonthCount(@RequestParam @NotNull String email, @RequestParam @NotNull String date) {
        MonthCountDto monthCountDto = friendTodoService.readMonthCount(email, date);

        return ResponseEntity.ok(DataResponseDto.of(monthCountDto, 200));
    }
}

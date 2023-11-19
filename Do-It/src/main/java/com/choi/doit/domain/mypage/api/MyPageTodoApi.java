package com.choi.doit.domain.mypage.api;

import com.choi.doit.domain.mypage.application.MyPageTodoService;
import com.choi.doit.domain.mypage.dto.response.CountFinishedTodoResponseDto;
import com.choi.doit.domain.mypage.dto.response.ReadTodoWithoutStatusListResponseDto;
import com.choi.doit.global.common.response.DataResponseDto;
import com.choi.doit.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/my-page/todo")
public class MyPageTodoApi {
    private final MyPageTodoService myPageTodoService;

    // 미완료 To-Do 존재 여부 조회
    @RequestMapping(method = RequestMethod.HEAD, value = "/unfinished")
    public ResponseEntity<ResponseDto> hasUnfinishedTodo() {
        myPageTodoService.hasUnfinishedTodo();

        return ResponseEntity.ok(ResponseDto.of(200));
    }

    // 미완료 To-Do 리스트 조회
    @GetMapping("/unfinished")
    public ResponseEntity<ResponseDto> readUnfinishedTodoList() {
        ReadTodoWithoutStatusListResponseDto readTodoWithoutStatusListResponseDto = myPageTodoService.readUnfinishedTodoList();

        return ResponseEntity.ok(DataResponseDto.of(readTodoWithoutStatusListResponseDto, 200));
    }

    @GetMapping("/finished/count")
    public ResponseEntity<ResponseDto> countFinishedTodo() {
        CountFinishedTodoResponseDto countFinishedTodoResponseDto = myPageTodoService.countFinishedTodo();

        return ResponseEntity.ok(DataResponseDto.of(countFinishedTodoResponseDto, 200));
    }

    @GetMapping("/finished/top2")
    public ResponseEntity<ResponseDto> readFinishedTodoTop2() {
        ReadTodoWithoutStatusListResponseDto readTodoWithoutStatusListResponseDto = myPageTodoService.readFinishedTodoListTop2();

        return ResponseEntity.ok(DataResponseDto.of(readTodoWithoutStatusListResponseDto, 200));
    }
}

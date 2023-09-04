package com.choi.doit.domain.mypage.api;

import com.choi.doit.domain.mypage.application.MyPageTodoService;
import com.choi.doit.domain.mypage.dto.response.CountFinishedTodoResponse;
import com.choi.doit.domain.mypage.dto.response.ReadTodoWithoutStatusListResponse;
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
@RequestMapping("/my-page/todo")
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
        ReadTodoWithoutStatusListResponse readTodoWithoutStatusListResponse = myPageTodoService.readUnfinishedTodoList();

        return ResponseEntity.ok(DataResponseDto.of(readTodoWithoutStatusListResponse, 200));
    }

    @GetMapping("/finished/count")
    public ResponseEntity<ResponseDto> countFinishedTodo() {
        CountFinishedTodoResponse countFinishedTodoResponse = myPageTodoService.countFinishedTodo();

        return ResponseEntity.ok(DataResponseDto.of(countFinishedTodoResponse, 200));
    }

    @GetMapping("/finished/top2")
    public ResponseEntity<ResponseDto> readFinishedTodoTop2() {
        ReadTodoWithoutStatusListResponse readTodoWithoutStatusListResponse = myPageTodoService.readFinishedTodoListTop2();

        return ResponseEntity.ok(DataResponseDto.of(readTodoWithoutStatusListResponse, 200));
    }
}

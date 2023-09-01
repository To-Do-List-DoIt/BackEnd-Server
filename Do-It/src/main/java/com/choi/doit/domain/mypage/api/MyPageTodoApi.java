package com.choi.doit.domain.mypage.api;

import com.choi.doit.domain.mypage.application.MyPageTodoService;
import com.choi.doit.domain.mypage.dto.response.HasUnfinishedTodoResponse;
import com.choi.doit.global.common.response.DataResponseDto;
import com.choi.doit.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/my-page/todo")
public class MyPageTodoApi {
    private final MyPageTodoService myPageTodoService;

    // 미완료 To-Do 존재 여부 조회
    @GetMapping("/exists/unfinished-todo")
    public ResponseEntity<ResponseDto> hasUnfinishedTodo() {
        HasUnfinishedTodoResponse hasUnfinishedTodoResponse = myPageTodoService.hasUnfinishedTodo();

        return ResponseEntity.ok(DataResponseDto.of(hasUnfinishedTodoResponse, 200));
    }
}

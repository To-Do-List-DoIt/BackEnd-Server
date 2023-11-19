package com.choi.doit.domain.mypage.api;

import com.choi.doit.domain.mypage.application.MyPageService;
import com.choi.doit.domain.mypage.dto.request.EditEmailRequestDto;
import com.choi.doit.domain.mypage.dto.request.EditPasswordRequestDto;
import com.choi.doit.global.common.response.DataResponseDto;
import com.choi.doit.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/my-page")
public class MyPageApi {
    private final MyPageService myPageService;

    // 계정 삭제
    @DeleteMapping("/user")
    public ResponseEntity<ResponseDto> deleteUser() {
        myPageService.deleteUser();

        return ResponseEntity.ok(ResponseDto.of(200));
    }

    // 프로필 이미지 변경
    @PatchMapping("/profile-image")
    public ResponseEntity<ResponseDto> editProfileImage(@RequestPart MultipartFile profile) throws IOException {
        String path = myPageService.setProfileImage(profile);

        return ResponseEntity.created(URI.create(path)).body(ResponseDto.of(201));
    }

    // 이메일 변경
    @PatchMapping("/email")
    public ResponseEntity<ResponseDto> editEmail(@Valid @RequestBody EditEmailRequestDto editEmailRequestDto) {
        String result = myPageService.setEmail(editEmailRequestDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(result, 201));
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<ResponseDto> editPassword(@Valid @RequestBody EditPasswordRequestDto editEmailRequestDto) {
        myPageService.setPassword(editEmailRequestDto);

        return ResponseEntity.status(201).body(ResponseDto.of(201));
    }
}

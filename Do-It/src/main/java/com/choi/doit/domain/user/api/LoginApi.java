package com.choi.doit.domain.user.api;

import com.choi.doit.domain.user.application.LoginService;
import com.choi.doit.domain.user.dto.response.LoginResponseDto;
import com.choi.doit.global.common.response.DataResponseDto;
import com.choi.doit.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class LoginApi {
    private final LoginService loginService;

    // 게스트 로그인
    @PostMapping("/guest")
    public ResponseEntity<ResponseDto> startAsGuest() {
        LoginResponseDto loginResponseDto = loginService.guestLogin();

        return ResponseEntity.status(201).body(DataResponseDto.of(loginResponseDto, 201));
    }

    // 로그아웃
    @DeleteMapping("/logout")
    public ResponseEntity<ResponseDto> logout(@RequestHeader @Value("Authorization") String authorization) {
        loginService.logout();

        return ResponseEntity.ok(ResponseDto.of(200));
    }
}

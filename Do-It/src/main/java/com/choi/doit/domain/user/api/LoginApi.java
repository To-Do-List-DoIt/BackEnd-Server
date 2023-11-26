package com.choi.doit.domain.user.api;

import com.choi.doit.domain.user.application.LoginService;
import com.choi.doit.domain.user.dto.response.GuestLoginResponseDto;
import com.choi.doit.global.common.response.DataResponseDto;
import com.choi.doit.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class LoginApi {
    private final LoginService loginService;

    // 게스트 로그인
    @PostMapping(value = {"/guest", "/guest/{user-id}"})
    public ResponseEntity<ResponseDto> startAsGuest(@PathVariable(required = false, value = "user-id") Long userId, @RequestParam(required = false, value = "code") String code) {
        GuestLoginResponseDto guestLoginResponseDto = loginService.guestLogin(userId, code);

        return ResponseEntity.status(201).body(DataResponseDto.of(guestLoginResponseDto, 201));
    }

    // 로그아웃
    @DeleteMapping("/logout")
    public ResponseEntity<ResponseDto> logout() {
        loginService.logout();

        return ResponseEntity.ok(ResponseDto.of(200));
    }
}

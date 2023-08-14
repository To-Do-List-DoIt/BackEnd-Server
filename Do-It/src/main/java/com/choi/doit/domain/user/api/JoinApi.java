package com.choi.doit.domain.user.api;

import com.choi.doit.domain.user.application.EmailJoinService;
import com.choi.doit.domain.user.dto.request.*;
import com.choi.doit.domain.user.dto.response.EmailAuthInfoResponseDto;
import com.choi.doit.domain.user.dto.response.EmailAuthResponseDto;
import com.choi.doit.domain.user.dto.response.EmailJoinResponseDto;
import com.choi.doit.domain.user.exception.UserErrorCode;
import com.choi.doit.global.common.response.DataResponseDto;
import com.choi.doit.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class JoinApi {
    private final EmailJoinService emailJoinService;

    // 이메일로 인증 링크 발송
    @PostMapping("/email/link")
    public ResponseEntity<ResponseDto> postEmailForLink(@Valid EmailRequestDto emailRequestDto) {
        EmailAuthResponseDto emailAuthResponseDto = emailJoinService.sendLink(emailRequestDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(emailAuthResponseDto, 201));
    }

    // 이메일 인증 링크 변경
    @PatchMapping("/email/link")
    public ResponseEntity<ResponseDto> patchEmailForLink(@RequestBody @Valid EmailAuthChangeRequestDto emailAuthChangeRequestDto) {
        EmailAuthResponseDto emailAuthResponseDto = emailJoinService.changeLink(emailAuthChangeRequestDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(emailAuthResponseDto, 201));
    }

    // 사용자가 링크 클릭 시
    @GetMapping("/email/link-confirm")
    public ResponseEntity<ResponseDto> emailConfirm(@Valid EmailAuthConfirmRequestDto emailAuthConfirmRequestDto) {
        try {
            // 인증 정보 확인
            emailJoinService.confirmEmail(emailAuthConfirmRequestDto);
        } catch (Exception e) {
            String message = e.getMessage();

            if (message.equals(UserErrorCode.INVALID_LINK.getMessage()))
                return ResponseEntity.status(401).body(ResponseDto.of(UserErrorCode.INVALID_LINK));
            else if (message.equals(UserErrorCode.EMAIL_ALREADY_AUTHENTICATED.getMessage()))
                return ResponseEntity.status(403).body(ResponseDto.of(UserErrorCode.EMAIL_ALREADY_AUTHENTICATED));
        }

        return ResponseEntity.ok(ResponseDto.of(200));
    }

    // 이메일 인증 여부 확인
    @GetMapping("/email/link")
    public ResponseEntity<ResponseDto> checkAuthInfo(@Valid EmailRequestDto emailRequestDto) {
        // dto 생성
        EmailAuthInfoResponseDto emailAuthInfoResponseDto = emailJoinService.checkAuthInfo(emailRequestDto);

        return ResponseEntity.ok().body(DataResponseDto.of(emailAuthInfoResponseDto, 200));
    }

    // 이메일, 닉네임 중복확인
    @GetMapping("/check")
    public ResponseEntity<ResponseDto> checkDuplicate(DuplicateCheckRequestDto dto) {
        emailJoinService.checkDuplicate(dto);

        return ResponseEntity.ok(ResponseDto.of(200));
    }

    // 이메일 가입
    @PostMapping("/email/new")
    public ResponseEntity<ResponseDto> join(@ModelAttribute @Valid EmailJoinRequestDto emailJoinRequestDto) throws IOException {
        EmailJoinResponseDto emailJoinResponseDto = emailJoinService.join(emailJoinRequestDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(emailJoinResponseDto, 201));
    }
}

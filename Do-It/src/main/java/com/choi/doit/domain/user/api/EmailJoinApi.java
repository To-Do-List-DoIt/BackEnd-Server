package com.choi.doit.domain.user.api;

import com.choi.doit.domain.user.application.EmailService;
import com.choi.doit.domain.user.dto.AuthInfoResponseDto;
import com.choi.doit.domain.user.dto.AuthLinkResponseDto;
import com.choi.doit.domain.user.exception.UserErrorCode;
import com.choi.doit.domain.user.vo.EmailAuthPatchVo;
import com.choi.doit.domain.user.vo.EmailAuthVo;
import com.choi.doit.domain.user.vo.EmailConfirmVo;
import com.choi.doit.domain.user.vo.EmailJoinVo;
import com.choi.doit.global.common.response.DataResponseDto;
import com.choi.doit.global.common.response.ResponseDto;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.DuplicateCheckHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/email")
public class EmailJoinApi {
    private final DuplicateCheckHandler duplicateCheckHandler;
    private final EmailService emailService;

    // 이메일로 인증 링크 발송
    @PostMapping("/link")
    public ResponseEntity<ResponseDto> postEmailForLink(@Valid EmailAuthVo emailAuthVo) {
        String email = emailAuthVo.getEmail();

        // 이메일 중복 체크
        duplicateCheckHandler.isDupEmail(email);

        // 인증 링크 발송
        String code = emailService.sendMail(email);
        AuthLinkResponseDto authLinkResponseDto = new AuthLinkResponseDto(code);

        // Redis 인증 정보 저장
        emailService.saveMailAuthInfo(email, code, false);

        return ResponseEntity.status(201).body(DataResponseDto.of(authLinkResponseDto, 201));
    }

    // 이메일 인증 링크 변경
    @PatchMapping("/link")
    public ResponseEntity<ResponseDto> patchEmailForLink(@RequestBody @Valid EmailAuthPatchVo emailAuthPatchVo) {
        String prev_email = emailAuthPatchVo.getPrev_email();
        String code = emailAuthPatchVo.getCode();
        String new_email = emailAuthPatchVo.getNew_email();

        // 이전 데이터 확인
        emailService.checkPrevAuthInfo(prev_email, code);

        // 이전 데이터 삭제
        emailService.deleteAuthInfo(prev_email);

        // 이메일 중복 체크
        duplicateCheckHandler.isDupEmail(new_email);

        // 인증 링크 발송
        String new_code = emailService.sendMail(new_email);
        AuthLinkResponseDto authLinkResponseDto = new AuthLinkResponseDto(new_code);

        // Redis 인증 정보 저장
        emailService.saveMailAuthInfo(new_email, new_code, false);

        return ResponseEntity.status(201).body(DataResponseDto.of(authLinkResponseDto, 201));
    }

    // 사용자가 링크 클릭 시
    @GetMapping("/link-confirm")
    public ResponseEntity<ResponseDto> emailConfirm(@Valid EmailConfirmVo emailConfirmVo) {
        String email = emailConfirmVo.getEmail();
        String code = emailConfirmVo.getCode();

        try {
            // 인증 정보 확인
            emailService.checkEmailLink(email, code);
        } catch (Exception e) {
            String message = e.getMessage();

            if (message.equals(UserErrorCode.INVALID_LINK.getMessage()))
                return ResponseEntity.status(401).body(ResponseDto.of(UserErrorCode.INVALID_LINK));
            else if (message.equals(UserErrorCode.EMAIL_ALREADY_AUTHORIZED.getMessage()))
                return ResponseEntity.status(403).body(ResponseDto.of(UserErrorCode.EMAIL_ALREADY_AUTHORIZED));
        }

        // Redis 인증 정보 저장
        emailService.saveMailAuthInfo(email, code, true);

        return ResponseEntity.ok(ResponseDto.of(200));
    }

    // 이메일 인증 여부 확인
    @GetMapping("/check")
    public ResponseEntity<ResponseDto> checkAuthInfo(@Valid EmailAuthVo emailAuthVo) {
        String email = emailAuthVo.getEmail();

        // 인증 여부 조회
        boolean isAuth = emailService.checkAuthInfo(email);

        // dto 생성
        AuthInfoResponseDto authInfoResponseDto = new AuthInfoResponseDto(email, isAuth);

        return ResponseEntity.ok().body(DataResponseDto.of(authInfoResponseDto, 200));
    }

    // 이메일 가입
    @PostMapping
    public ResponseEntity<ResponseDto> join(@ModelAttribute @Valid EmailJoinVo emailJoinVo) throws IOException {
        String email = emailJoinVo.getEmail();
        String nickname = emailJoinVo.getNickname();
        String raw_password = emailJoinVo.getPassword();
        MultipartFile profile = emailJoinVo.getProfile();

        log.info(email);

        // 인증 여부 조회
        boolean isAuth = emailService.checkAuthInfo(email);
        if (!isAuth)
            throw new RestApiException(UserErrorCode.UNAUTHORIZED_EMAIL);

        // 데이터 저장
        emailService.addUser(email, nickname, raw_password, profile);

        return ResponseEntity.status(201).body(ResponseDto.of(201));
    }
}

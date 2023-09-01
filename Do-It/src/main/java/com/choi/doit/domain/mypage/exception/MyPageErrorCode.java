package com.choi.doit.domain.mypage.exception;

import com.choi.doit.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MyPageErrorCode implements ErrorCode {
    EMAIL_CHANGE_FORBIDDEN(HttpStatus.FORBIDDEN, "OAuth user cannot change email."),
    EMAIL_UNCHANGED(HttpStatus.BAD_REQUEST, "Email unchanged."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "Email already exists.");

    private final HttpStatus httpStatus;
    private final String message;

    MyPageErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

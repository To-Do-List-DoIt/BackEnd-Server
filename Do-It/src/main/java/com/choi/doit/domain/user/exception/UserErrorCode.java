package com.choi.doit.domain.user.exception;

import com.choi.doit.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "Login failed."),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "Invalid type."),
    UNAUTHENTICATED_EMAIL(HttpStatus.UNAUTHORIZED, "Email authentication required."),
    INVALID_CODE(HttpStatus.UNAUTHORIZED, "Invalid code."),
    INVALID_EMAIL(HttpStatus.UNAUTHORIZED, "Invalid email."),
    INVALID_LINK(HttpStatus.UNAUTHORIZED, "Invalid link."),
    EMAIL_ALREADY_AUTHENTICATED(HttpStatus.FORBIDDEN, "Email already authenticated."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "Nickname already exists."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "Email already exists.");

    private final HttpStatus httpStatus;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

package com.choi.doit.domain.user.exception;

import com.choi.doit.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {
    UNAUTHORIZED_EMAIL(HttpStatus.UNAUTHORIZED, "Email authorization required."),
    INVALID_CODE(HttpStatus.UNAUTHORIZED, "Invalid code."),
    INVALID_EMAIL(HttpStatus.UNAUTHORIZED, "Invalid email."),
    INVALID_LINK(HttpStatus.UNAUTHORIZED, "Invalid link."),
    EMAIL_ALREADY_AUTHORIZED(HttpStatus.FORBIDDEN, "Email already authorized."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "Email already exists.");

    private final HttpStatus httpStatus;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

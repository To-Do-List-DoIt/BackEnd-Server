package com.choi.doit.domain.user.exception;

import com.choi.doit.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {
    EMAIL_NOT_EXIST(HttpStatus.BAD_REQUEST, "Email does not exist."),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "Invalid type."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "Email already exists."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "Nickname already exists."),
    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "User-id required."),
    UNAUTHENTICATED_EMAIL(HttpStatus.UNAUTHORIZED, "Email authentication required."),
    INVALID_CODE(HttpStatus.UNAUTHORIZED, "Invalid code."),
    INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid id-token."),
    INVALID_EMAIL(HttpStatus.UNAUTHORIZED, "Invalid email."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "Login failed."),
    EMAIL_ALREADY_AUTHENTICATED(HttpStatus.FORBIDDEN, "Email already authenticated."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found."),
    EMAIL_SENDING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Email Sending failed.");

    private final HttpStatus httpStatus;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

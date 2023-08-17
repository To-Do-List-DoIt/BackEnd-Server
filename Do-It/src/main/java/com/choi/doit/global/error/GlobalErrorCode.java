package com.choi.doit.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GlobalErrorCode implements ErrorCode {
    AUTHORIZATION_FAILED(HttpStatus.UNAUTHORIZED, "Authorization failed"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "Authentication failed"),
    LOGIN_REQUIRED(HttpStatus.FORBIDDEN, "Login required."),
    EXPIRED_JWT(HttpStatus.FORBIDDEN, "Token expired."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token."),
    TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "Access Token required."),
    FILE_DELETION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "File deletion failed."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "Invalid file format. Profile image must be image file."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Request method is not supported."),
    VALUE_REQUIRED(HttpStatus.BAD_REQUEST, "Value required."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "Invalid email format."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "User not found.");

    private final HttpStatus httpStatus;
    private final String message;

    GlobalErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

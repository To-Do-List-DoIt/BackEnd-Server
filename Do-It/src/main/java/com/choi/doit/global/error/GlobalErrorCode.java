package com.choi.doit.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GlobalErrorCode implements ErrorCode {
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "Invalid password format. (no space, 8-20 words)"),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "Invalid nickname format. (no space, 3-10 words)"),
    INVALID_TIME_FORMAT(HttpStatus.BAD_REQUEST, "Invalid time format. (Time format - HH:MM)"),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "Invalid date format. (Date format - YY-MM-DD)"),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "Invalid file format. Profile image must be image file."),
    VALUE_REQUIRED(HttpStatus.BAD_REQUEST, "Value required."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "Invalid email format."),
    AUTHORIZATION_FAILED(HttpStatus.UNAUTHORIZED, "Authorization failed."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "Authentication failed."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "Login failed."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token."),
    ACCESS_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "Access Token required."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "Access Token required."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "User not found."),
    LOGIN_REQUIRED(HttpStatus.FORBIDDEN, "Login required."),
    EXPIRED_JWT(HttpStatus.FORBIDDEN, "Token expired."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Request method is not supported."),
    FILE_DELETION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "File deletion failed."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    GlobalErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

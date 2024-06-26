package com.choi.doit.domain.friend.exception;

import com.choi.doit.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FriendErrorCode implements ErrorCode {
    REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "Request required."),
    TARGET_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "Target email not found."),
    GUEST_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "Guest user is not allowed to access friends."),
    TARGET_FORBIDDEN(HttpStatus.FORBIDDEN, "<Target is guest user.> Guest user is not allowed to access friends."),
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "Friend not found."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    FriendErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

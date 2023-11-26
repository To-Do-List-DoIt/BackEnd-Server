package com.choi.doit.domain.todo.exception;

import com.choi.doit.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum TodoErrorCode implements ErrorCode {
    CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "Category not found."),
    TODO_NOT_FOUND(HttpStatus.BAD_REQUEST, "Todo not found."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied.");

    private final HttpStatus httpStatus;
    private final String message;

    TodoErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

package com.choi.doit.global.error;

import com.choi.doit.global.common.response.ResponseDto;
import com.choi.doit.global.error.exception.RestApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // RuntimeException
    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ResponseDto> exceptionHandler(RestApiException e) {
        HttpStatus httpStatus = e.getErrorCode().getHttpStatus();
        String message = e.getMessage();

        e.printStackTrace();

        return ResponseEntity.status(httpStatus.value()).body(ResponseDto.of(httpStatus.value(), message));
    }

    // InvalidArgumentException
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ResponseDto> exceptionHandler(MethodArgumentNotValidException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String org_message = e.getMessage();
        String message = e.getMessage();

        if (org_message.contains("email"))
            message = GlobalErrorCode.INVALID_EMAIL_FORMAT.getMessage();

        if (org_message.contains("null"))
            message = GlobalErrorCode.VALUE_REQUIRED.getMessage();

        e.printStackTrace();

        return ResponseEntity.badRequest().body(ResponseDto.of(httpStatus.value(), message));
    }

    // MethodNotAllowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDto> exceptionHandler(HttpRequestMethodNotSupportedException e) {
        HttpStatus httpStatus = GlobalErrorCode.METHOD_NOT_ALLOWED.getHttpStatus();
        String message = e.getMessage();

        e.printStackTrace();

        return ResponseEntity.status(httpStatus.value()).body(ResponseDto.of(httpStatus.value(), message));
    }

    // InternalServerError
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> exceptionHandler(Exception e) {
        HttpStatus httpStatus = GlobalErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus();
        String message = httpStatus.getReasonPhrase();

        e.printStackTrace();

        return ResponseEntity.status(httpStatus.value()).body(ResponseDto.of(httpStatus.value(), message));
    }
}

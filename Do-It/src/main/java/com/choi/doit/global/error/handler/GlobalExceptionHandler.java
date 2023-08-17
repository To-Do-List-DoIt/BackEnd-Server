package com.choi.doit.global.error.handler;

import com.choi.doit.global.common.response.ResponseDto;
import com.choi.doit.global.error.GlobalErrorCode;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.error.exception.SpringSecurityException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

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

    // AuthenticationServiceException
    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ResponseDto> exceptionHandler(AuthenticationServiceException e) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        String message = e.getMessage();

        e.printStackTrace();

        return ResponseEntity.status(httpStatus.value()).body(ResponseDto.of(httpStatus.value(), message));
    }

    // SpringSecurityException
    @ExceptionHandler(SpringSecurityException.class)
    public ResponseEntity<ResponseDto> exceptionHandler(SpringSecurityException e) {
        HttpStatus httpStatus = e.getErrorCode().getHttpStatus();
        String message = e.getMessage();

        e.printStackTrace();

        return ResponseEntity.status(httpStatus.value()).body(ResponseDto.of(httpStatus.value(), message));
    }

    // ExpiredJwtException
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ResponseDto> exceptionHandler(ExpiredJwtException e) {
        HttpStatus httpStatus = GlobalErrorCode.EXPIRED_JWT.getHttpStatus();
        String message = GlobalErrorCode.EXPIRED_JWT.getMessage();

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

    // IOException
    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<ResponseDto> exceptionHandler(IOException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = httpStatus.getReasonPhrase();

        log.error(e.getMessage());
        e.printStackTrace();

        return ResponseEntity.internalServerError().body(ResponseDto.of(httpStatus.value(), message));
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

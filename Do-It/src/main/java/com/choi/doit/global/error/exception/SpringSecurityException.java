package com.choi.doit.global.error.exception;

import com.choi.doit.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class SpringSecurityException extends AuthenticationException {
    private final ErrorCode errorCode;

    public SpringSecurityException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

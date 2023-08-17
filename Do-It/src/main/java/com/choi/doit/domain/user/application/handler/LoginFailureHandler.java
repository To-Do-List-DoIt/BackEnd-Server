package com.choi.doit.domain.user.application.handler;

import com.choi.doit.global.error.GlobalErrorCode;
import com.choi.doit.global.error.exception.SpringSecurityException;
import com.choi.doit.global.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final ResponseUtil responseUtil;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws SpringSecurityException, IOException {
        responseUtil.setResponse(response, HttpServletResponse.SC_UNAUTHORIZED, GlobalErrorCode.AUTHENTICATION_FAILED.getMessage());
    }
}

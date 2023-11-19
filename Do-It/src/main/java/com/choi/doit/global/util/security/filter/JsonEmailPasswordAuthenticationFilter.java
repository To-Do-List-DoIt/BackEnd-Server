package com.choi.doit.global.util.security.filter;

import com.choi.doit.domain.user.application.LoginService;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.request.EmailLoginRequestDto;
import com.choi.doit.global.error.GlobalErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

@Slf4j
public class JsonEmailPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String DEFAULT_LOGIN_REQUEST_URL = "/user/login/**"; // "/user/login/email"으로 오는 요청을 처리
    private static final String HTTP_METHOD = "POST"; // 로그인 HTTP 메소드는 POST
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD); // "/login" + POST로 온 요청에 매칭된다.
    private final ObjectMapper objectMapper;
    private final LoginService loginService;

    public JsonEmailPasswordAuthenticationFilter(ObjectMapper objectMapper, LoginService loginService) {
        super("/user/login/**");
        this.objectMapper = objectMapper;
        this.loginService = loginService;
    }

    // 로그인 요청 처리
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method '" + request.getMethod() + "' not supported.");
        }

        String email, password;

        if (request.getRequestURI().contains("google")) {
            // google oauth
            String id_token = loginService.getIdToken(request);

            try {
                UserEntity googleUser = loginService.googleAuth(null, id_token);

                email = googleUser.getEmail();
                password = googleUser.getPassword();
            } catch (GeneralSecurityException e) {
                throw new AuthenticationServiceException(GlobalErrorCode.AUTHENTICATION_FAILED.getMessage());
            }
            //} else if (request.getRequestURI().contains("apple")) {
            // apple oauth
        } else {
            // 이메일 가입자
            String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

            @Valid
            EmailLoginRequestDto dto = objectMapper.readValue(messageBody, EmailLoginRequestDto.class);

            email = dto.getEmail();
            password = dto.getPassword();
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        return this.getAuthenticationManager().authenticate(authenticationToken);
    }
}

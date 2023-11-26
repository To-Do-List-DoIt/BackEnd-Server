package com.choi.doit.global.util.security.handler;

import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.response.LoginResponseDto;
import com.choi.doit.global.error.GlobalErrorCode;
import com.choi.doit.global.error.exception.SpringSecurityException;
import com.choi.doit.global.util.ResponseUtil;
import com.choi.doit.global.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final ResponseUtil responseUtil;
    private final JwtUtil jwtUtil;

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws SpringSecurityException, IOException {
        String email = extractUsername(authentication); // 인증 정보에서 Username(email) 추출
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() ->
                new SpringSecurityException(GlobalErrorCode.AUTHENTICATION_FAILED));

        // 토큰 발행
        LoginResponseDto dto = jwtUtil.generateTokens(user);

        // 응답 전송
        responseUtil.setDataResponse(response, HttpServletResponse.SC_CREATED, dto);
    }
}

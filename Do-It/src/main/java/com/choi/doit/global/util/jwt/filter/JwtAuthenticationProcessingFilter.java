package com.choi.doit.global.util.jwt.filter;

import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.response.LoginResponseDto;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.error.exception.SpringSecurityException;
import com.choi.doit.global.util.RandomUtil;
import com.choi.doit.global.util.ResponseUtil;
import com.choi.doit.global.util.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final ResponseUtil responseUtil;
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final String ACCESS_HEADER = "Authorization";

    // Decode Bearer
    private String decodeBearer(String bearer_token) {
        return Arrays.stream(bearer_token.split("Bearer ")).toList().get(1);
    }

    /*
    - Refresh token 존재 -> 토큰 재발급, filtering 종료
    - Refresh token 미존재 -> 유저 정보 저장, filtering 재개
    */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws RestApiException, SpringSecurityException, ServletException, IOException, ExpiredJwtException {
        // uri 검사하여 필터 통과 여부 결정
        String uri = request.getRequestURI();
        if (uri.contains("/login") || uri.contains("/sign-up") || uri.contains("/user/guest")) {
            filterChain.doFilter(request, response);
            return;
        }

        // refresh token 검증
        String refresh_token = jwtUtil.decodeHeader(false, request);

        // refresh token을 헤더에 가지고 있는 경우 token 재발급
        if (request.getRequestURI().equals("/user/token") && refresh_token != null) {
            UserEntity user = jwtUtil.validateRefreshToken(refresh_token);
            LoginResponseDto dto = jwtUtil.generateTokens(user);

            responseUtil.setDataResponse(response, HttpServletResponse.SC_CREATED, dto);

            return;
        }

        // refresh token을 헤더에 가지고 있지 않은 경우 access token 검증
        String access_token = jwtUtil.decodeHeader(true, request);
        UserEntity user = jwtUtil.validateAccessToken(access_token);

        saveAuthentication(user);

        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(UserEntity user) {
        String password = user.getPassword();
        if (password == null) {
            // 소셜 로그인 유저
            RandomUtil randomUtil = new RandomUtil();
            password = randomUtil.getRandomPassword(15, true);
        }

        UserDetails userDetails = User.builder()
                .username(user.getEmail())
                .password(password)
                .roles(user.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                password,
                authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        // context 초기화 후 인증 정보 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}

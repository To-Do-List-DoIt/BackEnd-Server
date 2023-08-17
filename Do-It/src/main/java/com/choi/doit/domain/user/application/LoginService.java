package com.choi.doit.domain.user.application;

import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.dto.response.LoginResponseDto;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.RandomUtil;
import com.choi.doit.global.util.RedisUtil;
import com.choi.doit.global.util.SecurityContextUtil;
import com.choi.doit.global.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RandomUtil randomUtil;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final SecurityContextUtil securityContextUtil;

    // 게스트 로그인
    public LoginResponseDto guestLogin() {
        // 랜덤 이메일 생성
        String email = randomUtil.getRandomUsername();
        String password = randomUtil.getRandomPassword(15);

        // 데이터 등록
        UserEntity user = userRepository.save(new UserEntity(email, passwordEncoder.encode(password)));

        return jwtUtil.generateTokens(user);
    }

    /*
    // 이메일 로그인
    public LoginResponseDto emailLogin(EmailLoginRequestDto dto) throws RestApiException {
        String email = dto.getEmail();
        String password = dto.getPassword();

        // Email 존재 여부
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(UserErrorCode.LOGIN_FAILED));

        // 비밀번호 일치 여부
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RestApiException(UserErrorCode.LOGIN_FAILED);

        // 토큰 발급
        return jwtUtil.generateTokens(user);
    }
     */

    // 로그아웃
    public void logout() throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();

        // Redis 데이터 삭제
        redisUtil.delete(user.getId() + "_refresh");

        // context holder 내의 인증 정보 삭제
        SecurityContextHolder.clearContext();
    }

    // 회원탈퇴
    public void deleteUser(String authorization) {
        UserEntity user = securityContextUtil.getUserEntity();

        // 게스트일 경우

        // 이메일 가입자일 경우

        // 소셜 가입자 - 애플

        // 소셜 가입자 - 구글
    }
}

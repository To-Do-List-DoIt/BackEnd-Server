package com.choi.doit.domain.user.application;

import com.choi.doit.domain.model.Provider;
import com.choi.doit.domain.model.Role;
import com.choi.doit.domain.todo.application.TodoDefaultSettingService;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.response.GuestLoginResponseDto;
import com.choi.doit.domain.user.dto.response.LoginResponseDto;
import com.choi.doit.domain.user.exception.UserErrorCode;
import com.choi.doit.domain.user.vo.OAuthUserInfoDto;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.oauth2.GoogleOAuth;
import com.choi.doit.global.util.RandomUtil;
import com.choi.doit.global.util.RedisUtil;
import com.choi.doit.global.util.SecurityContextUtil;
import com.choi.doit.global.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final RandomUtil randomUtil;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final SecurityContextUtil securityContextUtil;
    private final GoogleOAuth googleOAuth;
    private final TodoDefaultSettingService todoDefaultSettingService;

    public String getIdToken(HttpServletRequest request) throws AuthenticationServiceException {
        String id_token = Arrays.stream(request.getQueryString().split("id-token=")).toList().get(1);
        if (id_token == null) {
            throw new AuthenticationServiceException(UserErrorCode.INVALID_ID_TOKEN.getMessage());
        }

        return id_token;
    }

    @Transactional
    public UserEntity setGuestOAuthInfo(UserEntity user, OAuthUserInfoDto dto) {
        Long user_id = user.getId();

        userRepository.updateRole(Role.MEMBER, user_id);
        userRepository.updateEmail(dto.getEmail(), user_id);
        userRepository.updatePassword(dto.getPassword(), user_id);

        return userRepository.findByEmail(dto.getEmail()).orElse(null);
    }

    // 게스트 로그인
    @Transactional
    public GuestLoginResponseDto guestLogin(Long userId, String code) throws RestApiException {
        UserEntity user;
        String email;

        if (code == null) {
            // 랜덤 이메일 생성
            email = randomUtil.getRandomUsername();
            String password = randomUtil.getRandomPassword(15, true);

            // 데이터 등록
            user = userRepository.save(new UserEntity(email, password));

            // 기본 카테고리 생성
            todoDefaultSettingService.addDefaultCategory(user);
        } else {
            email = code;

            // user-id 유효성 검사
            if (userId == null) {
                throw new RestApiException(UserErrorCode.USER_ID_REQUIRED);
            }
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RestApiException(UserErrorCode.USER_NOT_FOUND));

            // 저장된 code 일치 여부 검사
            if (!email.equals(user.getEmail()))
                throw new RestApiException(UserErrorCode.INVALID_CODE);
        }

        // JWT token
        LoginResponseDto loginResponseDto = jwtUtil.generateTokens(user);

        return new GuestLoginResponseDto(loginResponseDto.getUser_id(), email, loginResponseDto.getAccess_token(), loginResponseDto.getRefresh_token());
    }

    // 구글 로그인
    @Transactional
    public UserEntity googleAuth(String authorization, String id_token) throws GeneralSecurityException, IOException {
        UserEntity user = null;
        if (authorization != null)
            user = jwtUtil.validateAccessToken(authorization);

        OAuthUserInfoDto dto = googleOAuth.authenticate(id_token);

        // google 서버에서 받아온 값
        Provider provider = dto.getProvider();
        String email = dto.getEmail();

        // 랜덤 값 생성
        String password = randomUtil.getRandomPassword(15, true);

        // 회원 데이터 조회, 새 회원이면 데이터 생성 + 기본 카테고리 데이터 생성
        if (user == null) {
            UserEntity newUser = userRepository.findByEmail(email)
                    .orElseGet(() -> userRepository.save(UserEntity.builder()
                            .email(email)
                            .password(password)
                            .provider(provider)
                            .build()));

            // 기본 카테고리 생성
            todoDefaultSettingService.addDefaultCategory(newUser);

            return newUser;
        } else {
            dto.setPassword(password);

            return setGuestOAuthInfo(user, dto);
        }
    }

    // 로그아웃
    @Transactional
    public void logout() throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();

        // Redis 데이터 삭제
        redisUtil.delete(user.getId() + "_refresh");

        // context holder 내의 인증 정보 삭제
        SecurityContextHolder.clearContext();
    }
}

package com.choi.doit.global.util.jwt;

import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.dto.response.LoginResponseDto;
import com.choi.doit.domain.user.vo.GenerateTokenVo;
import com.choi.doit.global.config.JwtProperties;
import com.choi.doit.global.error.GlobalErrorCode;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.error.exception.SpringSecurityException;
import com.choi.doit.global.util.RedisUtil;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.parser;

@RequiredArgsConstructor
@Component
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private final String BEARER = "Bearer ";
    private final String ACCESS_HEADER = "Authorization";
    private final String REFRESH_HEADER = "Authorization-refresh";

    // Generate Token
    public String generateToken(boolean isAccessToken, GenerateTokenVo vo) {
        // Payloads 생성
        Map<String, Object> payloads = new LinkedHashMap<>();
        payloads.put("user_id", vo.getId());
        payloads.put("email", vo.getEmail());

        // Expiration time
        Date now = new Date();
        Duration duration = isAccessToken ? Duration.ofMinutes(30) : Duration.ofDays(1); // 추후 acc.: 2h, ref.: 7d로 변경
        Date expiration = new Date(now.getTime() + duration.toMillis());

        // Subject
        String subject = isAccessToken ? "access" : "refresh";

        // Build
        return builder().setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(payloads)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setSubject(subject)
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(jwtProperties.getSecretKey().getBytes()))
                .compact();
    }

    public LoginResponseDto generateTokens(UserEntity user) {
        GenerateTokenVo vo = new GenerateTokenVo(user.getId(), user.getNickname());

        // 토큰 발급
        String access_token = generateToken(true, vo);
        String refresh_token = generateToken(false, vo);

        // Redis 저장
        redisUtil.opsForValueSet(user.getId() + "_refresh", refresh_token, 24);

        return new LoginResponseDto(user.getId(), access_token, refresh_token);
    }

    // Validate token
    public Map<String, Object> validateJwt(String jwt) {
        return parser()
                .setSigningKey(jwtProperties.getSecretKey().getBytes())
                .parseClaimsJws(jwt)
                .getBody();
    }

    // Decode Bearer
    public String decodeBearer(String bearer_token) {
        return Arrays.stream(bearer_token.split(BEARER)).toList().get(1);
    }

    // Decode request
    public Optional<String> decodeHeader(boolean isAccessToken, HttpServletRequest request) {
        String header = isAccessToken ? ACCESS_HEADER : REFRESH_HEADER;

        return Optional.ofNullable(request.getHeader(header))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.replace(BEARER, ""));
    }

    // Validate access token
    public UserEntity validateAccessToken(String access_token) throws RestApiException {
        // 검증 및 payload 추출
        Map<String, Object> payloads = validateJwt(access_token);

        // user 정보 존재 여부 검사
        UserEntity user = userRepository.findById(((Number) payloads.get("user_id")).longValue())
                .orElseThrow(() -> new RestApiException(GlobalErrorCode.USER_NOT_FOUND));

        // refresh token 존재 여부 검사
        String refresh = redisUtil.opsForValueGet(user.getId() + "_refresh");
        if (refresh == null)
            throw new RestApiException(GlobalErrorCode.LOGIN_REQUIRED);

        return user;
    }

    // Validate refresh token
    public UserEntity validateRefreshToken(String refresh_token) throws RestApiException, SpringSecurityException {
        // 검증 및 payload 추출
        Map<String, Object> payloads = validateJwt(refresh_token);

        // user 정보 존재 여부 검사
        UserEntity user = userRepository.findById(((Number) payloads.get("user_id")).longValue())
                .orElseThrow(() -> new RestApiException(GlobalErrorCode.USER_NOT_FOUND));

        // refresh token 존재 여부 검사
        String refresh = redisUtil.opsForValueGet(user.getId() + "_refresh");
        if (refresh == null)
            throw new RestApiException(GlobalErrorCode.TOKEN_REQUIRED);
        else if (!refresh.equals(refresh_token))
            throw new SpringSecurityException(GlobalErrorCode.AUTHENTICATION_FAILED);

        return user;
    }
}

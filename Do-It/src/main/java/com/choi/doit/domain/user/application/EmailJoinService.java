package com.choi.doit.domain.user.application;

import com.choi.doit.domain.model.EmailAuthKeyEnum;
import com.choi.doit.domain.model.Role;
import com.choi.doit.domain.todo.application.TodoDefaultSettingService;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.request.*;
import com.choi.doit.domain.user.dto.response.EmailAuthInfoResponseDto;
import com.choi.doit.domain.user.dto.response.EmailAuthResponseDto;
import com.choi.doit.domain.user.dto.response.EmailJoinResponseDto;
import com.choi.doit.domain.user.exception.UserErrorCode;
import com.choi.doit.domain.user.vo.EmailVo;
import com.choi.doit.domain.user.vo.NicknameVo;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.DuplicateCheckUtil;
import com.choi.doit.global.util.MailUtil;
import com.choi.doit.global.util.RandomUtil;
import com.choi.doit.global.util.RedisUtil;
import com.choi.doit.global.util.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailJoinService {
    @Value("${LINK_BASE_URL}")
    private String AUTH_LINK_BASE_URL;

    private final DuplicateCheckUtil duplicateCheckUtil;
    private final MailUtil mailUtil;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;
    private final SpringTemplateEngine springTemplateEngine;
    private final RandomUtil randomUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TodoDefaultSettingService todoDefaultSettingService;

    @Transactional
    public Long setGuestInfo(UserEntity user, EmailJoinRequestDto dto) {
        Long user_id = user.getId();

        user.updateRole(Role.MEMBER);
        user.updateEmail(dto.getEmail());
        user.updatePassword(dto.getPassword());
        user.updateNickname(dto.getNickname());

        return user_id;
    }

    // 인증 여부 검사
    public boolean isAuthenticated(String email) {
        Boolean is_authenticated = (Boolean) redisTemplate.opsForHash().get(email, EmailAuthKeyEnum.IS_AUTHENTICATED.getKey());

        if (is_authenticated == null)
            // 해당 이메일이 존재하지 않음
            throw new RestApiException(UserErrorCode.EMAIL_NOT_EXIST);

        return is_authenticated;
    }

    // Email과 Code 매칭
    private void matchEmailAndCode(@NotEmpty String email, @NotEmpty String code, boolean allowDuplicateRequest) {
        // Redis 데이터 조회
        String code_data = (String) redisTemplate.opsForHash().get(email, EmailAuthKeyEnum.CODE.getKey());
        Boolean is_authorized = (Boolean) redisTemplate.opsForHash().get(email, EmailAuthKeyEnum.IS_AUTHENTICATED.getKey());

        if (!code.equals(code_data))
            // 올바르지 않은 코드
            throw new RestApiException(UserErrorCode.INVALID_CODE);
        else if (allowDuplicateRequest && Boolean.TRUE.equals(is_authorized))
            // 링크 중복 클릭
            throw new RestApiException(UserErrorCode.EMAIL_ALREADY_AUTHENTICATED);
    }

    private String setContext(String link) {
        Context context = new Context();
        context.setVariable("link", link);
        return springTemplateEngine.process("email", context);
    }

    // Redis 인증 정보 저장
    public void saveMailAuthInfo(String email, String code, boolean isAuth) {
        Map<String, Object> value = new HashMap<>();
        value.put(EmailAuthKeyEnum.CODE.getKey(), code);
        value.put(EmailAuthKeyEnum.IS_AUTHENTICATED.getKey(), isAuth);

        redisUtil.opsForHashPut(email, value, 2);
    }

    // 이메일 링크 전송
    public EmailAuthResponseDto sendLink(EmailRequestDto emailRequestDto) {
        String email = emailRequestDto.getEmail();

        // 이메일 중복 체크
        duplicateCheckUtil.isDupEmail(email);

        // 인증 링크 발송
        String MAIL_SUBJECT = "[DO-IT] 이메일 인증";
        String code = randomUtil.getRandomCode(8);
        String link = AUTH_LINK_BASE_URL + "?email=" + email + "&code=" + code;
        mailUtil.sendMail(email, MAIL_SUBJECT, setContext(link));

        // Redis 인증 정보 저장
        saveMailAuthInfo(email, code, false);

        return new EmailAuthResponseDto(code);
    }

    // 이메일 링크 변경
    public EmailAuthResponseDto changeLink(EmailAuthChangeRequestDto emailAuthChangeRequestDto) {
        String prev_email = emailAuthChangeRequestDto.getPrev_email();
        String code = emailAuthChangeRequestDto.getCode();
        String new_email = emailAuthChangeRequestDto.getNew_email();

        // 이전 데이터 확인
        matchEmailAndCode(prev_email, code, false);

        // 이전 데이터 삭제
        redisTemplate.delete(prev_email);

        return sendLink(new EmailRequestDto(new_email));
    }

    // 사용자 이메일 인증 확인 링크
    public void confirmEmail(EmailAuthConfirmRequestDto emailAuthConfirmRequestDto) {
        String email = emailAuthConfirmRequestDto.getEmail();
        String code = emailAuthConfirmRequestDto.getCode();

        // code 일치 여부 확인
        matchEmailAndCode(email, code, true);

        // Redis 인증 정보 저장
        saveMailAuthInfo(email, code, true);
    }

    // 이메일 인증 여부 확인
    public EmailAuthInfoResponseDto checkAuthInfo(EmailRequestDto emailRequestDto) {
        String email = emailRequestDto.getEmail();

        // 인증 여부 조회
        return (new EmailAuthInfoResponseDto(email, isAuthenticated(email)));
    }

    // 이메일 가입
    public EmailJoinResponseDto join(String authorization, EmailJoinRequestDto emailJoinRequestDto) {
        UserEntity user = null;
        if (authorization != null)
            user = jwtUtil.validateAccessToken(jwtUtil.decodeBearer(authorization));

        String email = emailJoinRequestDto.getEmail();
        String password = emailJoinRequestDto.getPassword();
        String nickname = emailJoinRequestDto.getNickname();

        // 인증 여부 조회
        if (!isAuthenticated(emailJoinRequestDto.getEmail()))
            throw new RestApiException(UserErrorCode.UNAUTHENTICATED_EMAIL);

        // 이메일 중복 검사
        duplicateCheckUtil.isDupEmail(email);

        // 닉네임 중복 검사
        duplicateCheckUtil.isDupNickname(nickname);

        // 비밀번호 암호화
        emailJoinRequestDto.setPassword(passwordEncoder.encode(password));

        // 유저 데이터 저장 + 기본 카테고리 저장
        if (user == null) {
            UserEntity newUser = userRepository.save(emailJoinRequestDto.toEntity());

            // 기본 카테고리 저장
            todoDefaultSettingService.addDefaultCategory(newUser);

            return new EmailJoinResponseDto(newUser.getId());
        } else
            return new EmailJoinResponseDto(setGuestInfo(user, emailJoinRequestDto));
    }

    public void checkDuplicate(@Valid DuplicateCheckRequestDto dto) throws RestApiException {
        String type = dto.getType();
        String value = dto.getValue();

        if (type.equals("email")) {
            EmailVo vo = new EmailVo(value);
            duplicateCheckUtil.isDupEmail(vo);
        } else if (type.equals("nickname")) {
            NicknameVo vo = new NicknameVo(value);
            duplicateCheckUtil.isDupNickname(vo);
        } else {
            throw new RestApiException(UserErrorCode.INVALID_TYPE);
        }
    }
}

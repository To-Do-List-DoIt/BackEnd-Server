package com.choi.doit.domain.user.application;

import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.exception.UserErrorCode;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.ImageHandler;
import com.choi.doit.global.util.RandomCodeGenerator;
import com.choi.doit.global.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;
    private final RandomCodeGenerator codeGenerator;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisUtil redisUtil;
    private final ImageHandler imageHandler;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${LINK_BASE_URL}")
    private String BASE_URL;
    private final String KEY_CODE = "code";
    private final String KEY_IS_AUTHORIZED = "is_authorized";

    private String setContext(String link) {
        Context context = new Context();
        context.setVariable("link", link);
        return springTemplateEngine.process("email", context);
    }

    public String sendMail(String to) {
        String MAIL_SUBJECT = "[DO-IT] 이메일 인증";
        String code = codeGenerator.getRandomCode(8);
        String link = BASE_URL + "?email=" + to + "&code=" + code;
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(MAIL_SUBJECT);
            mimeMessageHelper.setText(setContext(link), true);

            javaMailSender.send(mimeMessage);

            log.info("[Mail] Email sent successfully. -- " + to);

            return code;
        } catch (MessagingException e) {
            log.error("[Mail] Email sending failed. -- " + to);
            throw new RuntimeException(e);
        }
    }

    // Redis 인증 정보 저장
    public void saveMailAuthInfo(String email, String code, boolean isAuth) {
        Map<String, Object> value = new HashMap<>();
        value.put(KEY_CODE, code);
        value.put(KEY_IS_AUTHORIZED, isAuth);

        redisUtil.redisOpsForHash(email, value, 2);
    }

    // 인증 이메일 변경 전 데이터 조회
    public void checkPrevAuthInfo(String prev_email, String code) throws RestApiException {
        // Redis 데이터 조회
        String code_data = (String) redisTemplate.opsForHash().get(prev_email, KEY_CODE);

        if (code_data == null) {
            // 해당 이메일이 존재하지 않음
            throw new RestApiException(UserErrorCode.INVALID_EMAIL);
        } else if (!code.equals(code_data))
            // 코드가 일치하지 않음
            throw new RestApiException(UserErrorCode.INVALID_CODE);
    }

    // Redis 인증 정보 삭제
    public void deleteAuthInfo(String email) {
        redisTemplate.delete(email);
    }

    // 인증 확인
    public void checkEmailLink(String email, String code) throws Exception {
        // Redis 데이터 조회
        String code_data = (String) redisTemplate.opsForHash().get(email, KEY_CODE);
        Boolean is_authorized = (Boolean) redisTemplate.opsForHash().get(email, KEY_IS_AUTHORIZED);

        if (!code.equals(code_data))
            // 올바르지 않은 링크
            throw new Exception(UserErrorCode.INVALID_LINK.getMessage());
        else if (Boolean.TRUE.equals(is_authorized))
            // 링크 중복 클릭
            throw new Exception(UserErrorCode.EMAIL_ALREADY_AUTHORIZED.getMessage());
    }

    // 인증 여부 검사
    public boolean checkAuthInfo(String email) {
        Boolean is_authorized = (Boolean) redisTemplate.opsForHash().get(email, KEY_IS_AUTHORIZED);

        if (is_authorized == null)
            // 해당 이메일이 존재하지 않음
            throw new RestApiException(UserErrorCode.UNAUTHORIZED_EMAIL);

        return is_authorized;
    }

    // 새로운 사용자 저장
    public void addUser(String email, String password, String nickname, MultipartFile image) throws IOException, RestApiException {
        String profile_path = null;

        // 프로필 이미지 저장
        if (!image.isEmpty())
            profile_path = imageHandler.saveProfileImage(email, image);

        // 비밀번호 암호화
        String encoded_password = passwordEncoder.encode(password);

        // 유저 객체 생성
        UserEntity user = new UserEntity(email, encoded_password, nickname, profile_path);

        // 유저 데이터 저장
        userRepository.save(user);
    }
}

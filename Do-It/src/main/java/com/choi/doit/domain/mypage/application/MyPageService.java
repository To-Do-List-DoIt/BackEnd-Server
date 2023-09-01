package com.choi.doit.domain.mypage.application;

import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.mypage.dto.EditEmailRequestDto;
import com.choi.doit.domain.mypage.dto.EditPasswordRequestDto;
import com.choi.doit.domain.mypage.exception.MyPageErrorCode;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.DuplicateCheckUtil;
import com.choi.doit.global.util.ImageHandler;
import com.choi.doit.global.util.RedisUtil;
import com.choi.doit.global.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final SecurityContextUtil securityContextUtil;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final ImageHandler imageHandler;
    private final DuplicateCheckUtil duplicateCheckUtil;
    private final PasswordEncoder passwordEncoder;

    // 계정 삭제
    public void deleteUser() {
        UserEntity user = securityContextUtil.getUserEntity();

        // 게스트 / 이메일 가입자일 경우
        // Redis 정보 삭제
        redisUtil.delete(user.getId() + "_refresh");
        // 데이터 삭제
        userRepository.delete(user);

        // 소셜 가입자 - 애플

        // 소셜 가입자 - 구글
    }

    // 프로필 이미지 변경
    public String setProfileImage(MultipartFile file) throws IOException, RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();

        if (user.getProfile_image_path() != null && file == null) {
            imageHandler.deleteProfileImage(user.getProfile_image_path());
            return null;
        }

        if (file == null)
            return null;

        String profile_path = imageHandler.saveProfileImage(user.getEmail(), file);
        user.updateProfileImage(profile_path);

        return profile_path;
    }

    // 이메일 변경
    public String setEmail(EditEmailRequestDto editEmailRequestDto) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        String new_email = editEmailRequestDto.getEmail();

        // OAuth 회원 여부 검사
        if (user.getProvider() != null) {
            throw new RestApiException(MyPageErrorCode.EMAIL_CHANGE_FORBIDDEN);
        }

        // 기존 이메일과 일치 여부 검사
        if (user.getEmail().equals(new_email)) {
            throw new RestApiException(MyPageErrorCode.EMAIL_UNCHANGED);
        }

        // 이메일 중복 확인
        duplicateCheckUtil.isDupEmail(new_email);

        // 데이터 업데이트
        user.updateEmail(new_email);

        return new_email;
    }

    // 비밀번호 변경
    public void setPassword(EditPasswordRequestDto editPasswordRequestDto) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        String new_password = editPasswordRequestDto.getPassword();

        // 비밀번호 암호화
        String encoded_password = passwordEncoder.encode(new_password);

        // 데이터 업데이트
        user.updatePassword(encoded_password);
    }
}

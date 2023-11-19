package com.choi.doit.global.util;

import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.exception.UserErrorCode;
import com.choi.doit.domain.user.vo.EmailVo;
import com.choi.doit.domain.user.vo.NicknameVo;
import com.choi.doit.global.error.exception.RestApiException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DuplicateCheckUtil {
    private final UserRepository userRepository;

    public void isDupEmail(String email) throws RestApiException {
        boolean isDup = userRepository.existsByEmail(email);

        if (isDup)
            throw new RestApiException(UserErrorCode.DUPLICATED_EMAIL);
    }

    public void isDupEmail(@Valid EmailVo vo) throws RestApiException {
        String email = vo.getEmail();
        boolean isDup = userRepository.existsByEmail(email);

        if (isDup)
            throw new RestApiException(UserErrorCode.DUPLICATED_EMAIL);
    }

    public void isDupNickname(String nickname) throws RestApiException {
        boolean isDup = userRepository.existsByNickname(nickname);

        if (isDup)
            throw new RestApiException(UserErrorCode.DUPLICATED_NICKNAME);
    }

    public void isDupNickname(@Valid NicknameVo vo) throws RestApiException {
        String nickname = vo.getNickname();
        boolean isDup = userRepository.existsByNickname(nickname);

        if (isDup)
            throw new RestApiException(UserErrorCode.DUPLICATED_NICKNAME);
    }
}

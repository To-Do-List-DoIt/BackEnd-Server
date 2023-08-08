package com.choi.doit.global.util;

import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.exception.UserErrorCode;
import com.choi.doit.global.error.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DuplicateCheckHandler {
    private final UserRepository userRepository;

    public void isDupEmail(String email) {
        boolean isDup = userRepository.existsByEmail(email);

        if (isDup)
            throw new RestApiException(UserErrorCode.DUPLICATED_EMAIL);
    }
}

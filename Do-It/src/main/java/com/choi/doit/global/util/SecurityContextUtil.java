package com.choi.doit.global.util;

import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.global.error.GlobalErrorCode;
import com.choi.doit.global.error.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityContextUtil {
    private final UserRepository userRepository;

    public UserEntity getUserEntity() throws RestApiException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new RestApiException(GlobalErrorCode.AUTHORIZATION_FAILED));
    }
}

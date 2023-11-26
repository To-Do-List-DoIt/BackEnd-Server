package com.choi.doit.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class LoginResponseDto {
    private Long userId;
    private String accessToken;
    private String refreshToken;
}

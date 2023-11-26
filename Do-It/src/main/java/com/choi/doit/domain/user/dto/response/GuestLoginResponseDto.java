package com.choi.doit.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GuestLoginResponseDto {
    private Long userId;
    private String userCode;
    private String accessToken;
    private String refreshToken;
}

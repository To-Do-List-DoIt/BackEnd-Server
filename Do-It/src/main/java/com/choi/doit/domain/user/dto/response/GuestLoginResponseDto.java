package com.choi.doit.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GuestLoginResponseDto {
    private Long user_id;
    private String code;
    private String access_token;
    private String refresh_token;
}

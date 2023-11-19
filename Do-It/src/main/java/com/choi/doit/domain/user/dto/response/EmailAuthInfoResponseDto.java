package com.choi.doit.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmailAuthInfoResponseDto {
    private String email;
    private boolean isAuthorized;
}

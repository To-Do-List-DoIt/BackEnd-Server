package com.choi.doit.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthInfoResponseDto {
    private String email;
    private boolean is_authorized;
}

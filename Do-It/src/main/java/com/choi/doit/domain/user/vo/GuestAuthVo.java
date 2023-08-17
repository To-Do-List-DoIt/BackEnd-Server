package com.choi.doit.domain.user.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GuestAuthVo {
    private String email;
    private String password;
}

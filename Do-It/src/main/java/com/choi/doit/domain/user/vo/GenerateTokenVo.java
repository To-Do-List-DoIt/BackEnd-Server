package com.choi.doit.domain.user.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GenerateTokenVo {
    private final Long id;
    private final String email;
}

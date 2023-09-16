package com.choi.doit.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailAuthKeyEnum {
    CODE("code"),
    IS_AUTHENTICATED("is_authenticated");

    private final String key;
}

package com.choi.doit.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    MEMBER("ROLE_MEMBER"),
    GUEST("ROLE_GUEST");

    private final String name;
}

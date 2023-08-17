package com.choi.doit.domain.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Provider {
    GOOGLE("google"),
    APPLE("apple");

    private final String name;
}

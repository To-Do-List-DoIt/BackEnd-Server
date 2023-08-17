package com.choi.doit.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.secret_key}")
    private String secretKey;
}

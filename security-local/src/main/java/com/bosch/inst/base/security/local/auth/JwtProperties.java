package com.bosch.inst.base.security.local.auth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("security-local")
@Component
@Getter
public class JwtProperties {
    @Value("${jwt.header:X-Access-Token}")
    private String header;

    @Value("${jwt.secret:MyJwtSecret}")
    private String secret;

    @Value("${jwt.expire:7200000}")
    private Long expire;
}

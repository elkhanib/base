package com.bosch.inst.base.security.im.auth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("security-im")
@Component
@Getter
public class CredentialsProperties {
    @Value("${jwt.header:X-Access-Token}")
    private String header;

    @Value("${jwt.secret:MyJwtSecret}")
    private String secret;

    @Value("${jwt.expire:7200000}")
    private Long expire;
}

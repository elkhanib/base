package com.bosch.inst.base.security.local.auth;

import lombok.Data;

@Data
public class JwtUser {
    private String password;
    private final String username;
}

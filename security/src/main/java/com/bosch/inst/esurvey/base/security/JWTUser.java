package com.bosch.inst.esurvey.base.security;

import lombok.Data;

@Data
public class JWTUser {
    private String password;
    private final String username;
}

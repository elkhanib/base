package com.bosch.inst.base.security.local.auth;

import lombok.Data;

@Data
public class Credentials {
    private String password;
    private final String username;
}

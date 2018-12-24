package com.bosch.inst.base.security.im.auth;

import lombok.Data;
import lombok.NonNull;

@Data
public class Credentials {
    private String tenant;
    @NonNull
    private String username;
    @NonNull
    private String password;
}

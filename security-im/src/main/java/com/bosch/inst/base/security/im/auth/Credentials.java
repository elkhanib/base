package com.bosch.inst.base.security.im.auth;

import lombok.Data;
import lombok.NonNull;
import org.springframework.context.annotation.Profile;

@Profile("security-im")
@Data
public class Credentials {
    private String tenant;
    @NonNull
    private String username;
    @NonNull
    private String password;
}

package com.bosch.inst.base.security.local.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface ISecurityProvider extends UserDetailsService {
    boolean validate(String username, String password);
}

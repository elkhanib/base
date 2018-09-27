package com.bosch.inst.base.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ISecurityProvider extends UserDetailsService {
    UserDetails authenticate(String username, String password);
}

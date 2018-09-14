package com.bosch.inst.esurvey.base.security.service.impl;

import com.bosch.inst.esurvey.base.security.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Username " + username + " is logging in");

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        System.err.println(encoder.encode(username));
        List<String> roles = new ArrayList<>();

        return new User(username, encoder.encode(username), createAuthorityList(roles));
    }

    /**
     * Create {@link GrantedAuthority} by a special role.
     *
     * @param roles the roles
     * @return a list of {@link GrantedAuthority}
     */
    public static List<GrantedAuthority> createAuthorityList(final Collection<String> roles) {
        final List<GrantedAuthority> authorities = new ArrayList<>(roles.size());

        for (final String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
            // add spring security ROLE authority which is indicated by the
            // `ROLE_` prefix
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        return authorities;
    }
}
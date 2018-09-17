package com.bosch.inst.base.security;

import com.bosch.inst.base.security.authorization.AuthProperties;
import com.bosch.inst.base.security.filter.BasicAuthenticationProcessingFilter;
import com.bosch.inst.base.security.filter.CookieAuthenticationProcessingFilter;
import com.bosch.inst.base.security.filter.TokenAuthenticationProcessingFilter;
import com.bosch.inst.base.security.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Can be used to create an own WebSecurityConfigurerAdapter that is pre-configured
 * with the ImAuthenticationProviderService and provides some methods for easier filter configuration.
 */
public abstract class ImWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Autowired
    private IUserService userService;

    @Autowired
    private AuthProperties authProperties;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.userDetailsService(userService).passwordEncoder(encoder);
    }

    protected BasicAuthenticationProcessingFilter getBasicAuthFilter(String defaultFilterProcessesUrl) throws Exception {
        BasicAuthenticationProcessingFilter filter = new
                BasicAuthenticationProcessingFilter(defaultFilterProcessesUrl);
        filter.setAuthenticationManager(this.authenticationManager());
        return filter;
    }

    protected TokenAuthenticationProcessingFilter getTokenAuthFilter(String defaultFilterProcessesUrl, String jwtSecret) throws
            Exception {
        TokenAuthenticationProcessingFilter filter = new
                TokenAuthenticationProcessingFilter(defaultFilterProcessesUrl, jwtSecret);
        filter.setAuthenticationManager(this.authenticationManager());
        return filter;
    }

    protected CookieAuthenticationProcessingFilter getCookieAuthFilter(String defaultFilterProcessesUrl, String jwtSecret) throws
            Exception {
        CookieAuthenticationProcessingFilter filter = new
                CookieAuthenticationProcessingFilter(defaultFilterProcessesUrl, authProperties, jwtSecret);
        filter.setAuthenticationManager(this.authenticationManager());
        return filter;
    }
}


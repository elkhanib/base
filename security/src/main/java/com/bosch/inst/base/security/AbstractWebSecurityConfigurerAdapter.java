package com.bosch.inst.base.security;

import com.bosch.inst.base.security.auth.AuthenticationProperties;
import com.bosch.inst.base.security.auth.AuthenticationProviderService;
import com.bosch.inst.base.security.filter.BasicAuthenticationProcessingFilter;
import com.bosch.inst.base.security.filter.CookieAuthenticationProcessingFilter;
import com.bosch.inst.base.security.filter.TokenAuthenticationProcessingFilter;
import com.bosch.inst.base.security.service.IUserProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Can be used to create an own WebSecurityConfigurerAdapter that is pre-configured
 * with the AuthenticationProviderService and provides some methods for easier filter config.
 */
public abstract class AbstractWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationProperties properties;

    @Autowired
    private AuthenticationProviderService authenticationProviderService;

    @Autowired
    private IUserProviderService userProviderService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        auth.userDetailsService(userProviderService).passwordEncoder(encoder);
        auth.authenticationProvider(authenticationProviderService);
    }

    protected BasicAuthenticationProcessingFilter getBasicAuthFilter(String defaultFilterProcessesUrl) throws Exception {
        BasicAuthenticationProcessingFilter filter = new
                BasicAuthenticationProcessingFilter(defaultFilterProcessesUrl);
        filter.setAuthenticationManager(this.authenticationManager());
        return filter;
    }

    protected TokenAuthenticationProcessingFilter getTokenAuthFilter(String defaultFilterProcessesUrl) throws
            Exception {
        TokenAuthenticationProcessingFilter filter = new
                TokenAuthenticationProcessingFilter(defaultFilterProcessesUrl);
        filter.setAuthenticationManager(this.authenticationManager());
        return filter;
    }

    protected CookieAuthenticationProcessingFilter getCookieAuthFilter(String defaultFilterProcessesUrl) throws
            Exception {
        CookieAuthenticationProcessingFilter filter = new
                CookieAuthenticationProcessingFilter(defaultFilterProcessesUrl, properties);
        filter.setAuthenticationManager(this.authenticationManager());
        return filter;
    }
}

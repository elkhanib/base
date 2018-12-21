package com.bosch.inst.base.security.local.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Custom authentication provider used to authenticate the /prometheus endpoint user. <br>
 * This releases IoT permissions form a large number of basic auth authentication requests.
 */
@Component
@Slf4j
public class PrometheusAuthenticationProviderService implements AuthenticationProvider {

    @Value("${prometheus.user:prometheus.user.unkonwn}")
    private String prometheusUser;

    @Value("${prometheus.password:prometheus.password.unknown}")
    private String prometheusPassword;

    private UsernamePasswordAuthenticationToken prometheusAuthenticationToken;

    public PrometheusAuthenticationProviderService() {
        createPrometheusToken();
    }

    private void createPrometheusToken() {
        SimpleGrantedAuthority access = new SimpleGrantedAuthority("ACCESS");
        SimpleGrantedAuthority actuator = new SimpleGrantedAuthority("ACTUATOR");
        List<SimpleGrantedAuthority> authorities = asList(access, actuator);
        prometheusAuthenticationToken =
                new UsernamePasswordAuthenticationToken(prometheusUser, prometheusPassword, authorities);
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (checkCredentials(authentication)) {
            log.debug("User {} authenticated. Provide authority 'ACCESS' and 'ACTUATOR'", authentication.getName());
            return prometheusAuthenticationToken;
        } else {
            log.debug("User {} unknown", authentication.getName());
            return null;
        }
    }

    private boolean checkCredentials(Authentication authentication) {
        String nameToCheck = authentication.getName();
        String passwordToCheck = authentication.getCredentials().toString();
        if (nameToCheck.equals(prometheusUser)) {
            if (passwordToCheck.equals(prometheusPassword)) {
                return true;
            } else {
                log.warn("User {} found but password does not match!", nameToCheck);
            }
        }
        return false;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}

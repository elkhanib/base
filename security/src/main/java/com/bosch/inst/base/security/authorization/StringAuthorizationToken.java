package com.bosch.inst.base.security.authorization;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Represents an Authentication that is only known as string.
 */
public class StringAuthorizationToken implements Authentication {
    private transient String authorizationToken;
    private transient Collection<GrantedAuthority> authorities;

    public StringAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public StringAuthorizationToken(String authorizationToken, Collection<GrantedAuthority> authorities) {
        this.authorizationToken = authorizationToken;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getCredentials() {
        return authorizationToken;
    }

    @Override
    public IAuthorizationToken getDetails() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return authorities != null;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    @Override
    public String getName() {
        return null;
    }
}

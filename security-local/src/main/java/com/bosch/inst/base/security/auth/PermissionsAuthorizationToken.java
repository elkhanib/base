package com.bosch.inst.base.security.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class PermissionsAuthorizationToken implements Authentication {
    private transient IAuthorizationToken authorizationToken;
    private transient Collection<GrantedAuthority> authorities;

    public PermissionsAuthorizationToken(IAuthorizationToken authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public PermissionsAuthorizationToken(IAuthorizationToken authorizationToken, Collection<GrantedAuthority> authorities) {
        this.authorizationToken = authorizationToken;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getCredentials() {
        return authorizationToken.getJwt();
    }

    @Override
    public IAuthorizationToken getDetails() {
        return authorizationToken;
    }

    @Override
    public String getPrincipal() {
        return authorizationToken.getUserId();
    }

    @Override
    public boolean isAuthenticated() {
        return authorities != null;
    }

    @Override
    public void setAuthenticated(boolean b) {
        throw new IllegalArgumentException();
    }

    @Override
    public String getName() {
        return authorizationToken.getAudience();
    }
}
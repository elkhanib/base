package com.bosch.inst.base.security.local.filter;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * Helps to authenticate requests
 */
public abstract class BaseAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private Authentication anonymous;


    public BaseAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);

        anonymous = new AnonymousAuthenticationToken("anonymous", "anonymous", Collections.singleton
                (new SimpleGrantedAuthority("ANONYMOUS")));
        setAuthenticationSuccessHandler(new AuthSuccess());
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        boolean requiresAuthentication = super.requiresAuthentication(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return requiresAuthentication;
        }
        return requiresAuthentication && authentication instanceof AnonymousAuthenticationToken;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain
            chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

    private static class AuthSuccess implements AuthenticationSuccessHandler {

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication
                authentication) throws IOException, ServletException {
            // Do nothing
        }
    }

    public Authentication getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Authentication anonymous) {
        this.anonymous = anonymous;
    }


}

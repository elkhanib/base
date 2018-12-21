package com.bosch.inst.base.security.local.filter;

import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Reads and parses the Authorization header
 */
public abstract class HeaderAuthenticationProcessingFilter extends BaseAuthenticationProcessingFilter {

    public HeaderAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            IOException, ServletException {

        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            return getAnonymous();
        }
        String[] parts = authorization.split(" ", 2);
        if (parts.length != 2 || !StringUtils.hasText(parts[0]) || !StringUtils.hasText(parts[1])) {
            return getAnonymous();
        }

        return authenticateToken(parts[0], parts[1]);
    }

    /**
     * Is called with the two parts from the Authorization header to run the authentication.
     *
     * @param type  the token type e.g. Bearer, Basic
     * @param token the actual token
     * @return Authentication object or null if the token type is unknown
     */
    public abstract Authentication authenticateToken(String type, String token);

}


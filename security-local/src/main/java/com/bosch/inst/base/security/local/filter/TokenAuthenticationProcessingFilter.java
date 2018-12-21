package com.bosch.inst.base.security.local.filter;


import com.bosch.inst.base.security.local.auth.StringAuthorizationToken;
import com.bosch.inst.base.security.local.config.ExpiredTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

/**
 * Checks if the Authorization Bearer header is set and authenticates the token.
 */
public class TokenAuthenticationProcessingFilter extends HeaderAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationProcessingFilter.class);

    private String jwtSecret;

    public TokenAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication authenticateToken(String type, String token) {
        try {
            if ("Bearer".equals(type)) {
                return this.getAuthenticationManager().authenticate(new StringAuthorizationToken(token));
                //  return getAuthentication(token);
            }
        } catch (ExpiredTokenException | IllegalArgumentException e) {
            LOG.debug("Token Header Authentication failed", e);
            return getAnonymous();
        }
        return getAnonymous();
    }
}
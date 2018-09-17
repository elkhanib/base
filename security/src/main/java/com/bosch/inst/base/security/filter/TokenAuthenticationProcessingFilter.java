package com.bosch.inst.base.security.filter;


import com.bosch.inst.base.security.configuration.ExpiredTokenException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;

/**
 * Checks if the Authorization Bearer header is set and authenticates the token.
 */
public class TokenAuthenticationProcessingFilter extends HeaderAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationProcessingFilter.class);

    private String jwtSecret;

    public TokenAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }


    public TokenAuthenticationProcessingFilter(String defaultFilterProcessesUrl, String jwtSecret) {
        super(defaultFilterProcessesUrl);
        this.jwtSecret = jwtSecret;
    }

    @Override
    public Authentication authenticateToken(String type, String token) {
        try {
            if ("Bearer".equals(type)) {
//                return this.getAuthenticationManager().authenticate(new StringAuthorizationToken(token));
                return getAuthentication(token);
            }
        } catch (ExpiredTokenException | IllegalArgumentException e) {
            LOG.debug("Token Header Authentication failed", e);
            return getAnonymous();
        }
        return getAnonymous();
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }

}
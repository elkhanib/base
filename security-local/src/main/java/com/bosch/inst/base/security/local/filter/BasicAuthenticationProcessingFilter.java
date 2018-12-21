package com.bosch.inst.base.security.local.filter;

import com.bosch.inst.base.security.local.config.ExpiredTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;

import java.io.IOException;

/**
 * Checks if the Authorization Basic header is set
 */
public class BasicAuthenticationProcessingFilter extends HeaderAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(BasicAuthenticationProcessingFilter.class);

    public BasicAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication authenticateToken(String type, String token) {
        try {
            if ("Basic".equals(type)) {
                return authenticateBasic(token);
            }
        } catch (ExpiredTokenException | IllegalArgumentException | IOException e) {
            LOG.debug("Basic Header Authentication failed", e);
            return getAnonymous();
        }
        return getAnonymous();
    }


    private Authentication authenticateBasic(String base64UserPass) throws IOException {
        String[] userPass = extractAndDecodeHeader(base64UserPass);

        return this.getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(userPass[0], userPass[1]));
    }

    /**
     * Implementation derived from
     * org.springframework.security.web.authentication.www.BasicAuthenticationFilter
     */
    private String[] extractAndDecodeHeader(String header) {
        byte[] decoded;
        String token;
        try {
            byte[] base64Token = header.getBytes("UTF-8");
            decoded = Base64.decode(base64Token);
            token = new String(decoded, "UTF-8");
        } catch (IllegalArgumentException | IOException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token", e);
        }

        int delim = token.indexOf(':');

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }

}
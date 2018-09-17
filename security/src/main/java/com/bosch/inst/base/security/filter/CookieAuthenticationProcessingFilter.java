package com.bosch.inst.base.security.filter;

import com.bosch.inst.base.security.authorization.AuthProperties;
import com.bosch.inst.base.security.configuration.ExpiredTokenException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Checks if the TOKEN_COOKIE is set and verifies the token in the cookie.
 * If the cookies is not valid, it will be removed
 */
public class CookieAuthenticationProcessingFilter extends BaseAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CookieAuthenticationProcessingFilter.class);

    private AuthProperties authProperties;

    private String cookieName;

    private String jwtSecret;

    public CookieAuthenticationProcessingFilter(String defaultFilterProcessesUrl, AuthProperties authProperties, String jwtSecret) {
        super(defaultFilterProcessesUrl);
        this.authProperties = authProperties;
        this.cookieName = authProperties.getCookie().getName();
        this.jwtSecret = jwtSecret;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            IOException, ServletException {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return getAnonymous(); // No Cookies, try next filter
        }

        Cookie cookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals(cookieName)).findFirst()
                .orElse(null);
        if (cookie == null || !StringUtils.hasText(cookie.getValue())) {
            return getAnonymous(); // No Cookies with a value, try next filter
        }

        try {
//            return this.getAuthenticationManager().authenticate(new StringAuthorizationToken(cookie.getValue()));
            return getAuthentication(request, cookie.getValue());
        } catch (ExpiredTokenException | IllegalArgumentException e) {
            LOG.debug("Cookie Authentication failed", e);
            response.addCookie(getLogoutCookie());
            return getAnonymous();
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, String token) {
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

    private Cookie getLogoutCookie() {
        Cookie cookie = new Cookie(cookieName, null);
        Optional.ofNullable(authProperties.getCookie().getHttpOnly()).ifPresent(cookie::setHttpOnly);
        Optional.ofNullable(authProperties.getCookie().getDomain()).ifPresent(cookie::setDomain);
        Optional.ofNullable(authProperties.getCookie().getPath()).ifPresent(cookie::setPath);
        Optional.ofNullable(authProperties.getCookie().getSecure()).ifPresent(cookie::setSecure);
        cookie.setMaxAge(0);
        return cookie;
    }


}

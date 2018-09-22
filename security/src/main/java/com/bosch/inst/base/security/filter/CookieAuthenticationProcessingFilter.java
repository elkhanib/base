package com.bosch.inst.base.security.filter;

import com.bosch.inst.base.security.auth.AuthProperties;
import com.bosch.inst.base.security.auth.StringAuthorizationToken;
import com.bosch.inst.base.security.config.ExpiredTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Checks if the TOKEN_COOKIE is set and verifies the token in the cookie.
 * If the cookies is not valid, it will be removed
 */
public class CookieAuthenticationProcessingFilter extends BaseAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CookieAuthenticationProcessingFilter.class);

    private AuthProperties properties;

    private String cookieName;


    public CookieAuthenticationProcessingFilter(String defaultFilterProcessesUrl, AuthProperties authProperties) {
        super(defaultFilterProcessesUrl);
        this.properties = authProperties;
        this.cookieName = authProperties.getCookie().getName();
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
            return this.getAuthenticationManager().authenticate(new StringAuthorizationToken(cookie.getValue()));
//            return getAuthentication(request, cookie.getValue());
        } catch (ExpiredTokenException | IllegalArgumentException e) {
            LOG.debug("Cookie Authentication failed", e);
            response.addCookie(getLogoutCookie());
            return getAnonymous();
        }
    }

    private Cookie getLogoutCookie() {
        Cookie cookie = new Cookie(cookieName, null);
        Optional.ofNullable(properties.getCookie().getHttpOnly()).ifPresent(cookie::setHttpOnly);
        Optional.ofNullable(properties.getCookie().getDomain()).ifPresent(cookie::setDomain);
        Optional.ofNullable(properties.getCookie().getPath()).ifPresent(cookie::setPath);
        Optional.ofNullable(properties.getCookie().getSecure()).ifPresent(cookie::setSecure);
        cookie.setMaxAge(0);
        return cookie;
    }


}

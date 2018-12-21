package com.bosch.inst.base.security.im.cookie;

import com.bosch.im.api2.jwt.IJsonWebToken;
import com.bosch.im.spring.config.AuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static org.joda.time.Minutes.minutes;

/**
 * Handler for working with the authorization cookie.<br>
 * The authorization cookie is used to login the user 'into' PLCS. It is initially provided by the
 * {@link LoginController} at the {@code /login} endpoint where the user logs in with its credentials. <br>
 * It is renewed on each successful request by the {@link RefreshLoginCookieFilter}.
 * <ul>
 * <li>The name of the token is defined by {@link AuthProperties.Cookie}</li>
 * <li>The value is the JWT received by IoT Permissions and stored in the Spring SecurityContext</li>
 * </ul>
 *
 * @see LoginController
 * @see RefreshLoginCookieFilter
 * @see SecurityConfiguration
 */
@Profile("security-im")
@Slf4j
@Service
public class AuthorizationCookieHandler {

    private static final int MAX_AGE = minutes(15).toStandardSeconds().getSeconds();

    private AuthProperties.Cookie cookieProperties;

    @Autowired
    public AuthorizationCookieHandler(AuthProperties authProperties) {
        cookieProperties = authProperties.getCookie();
    }

    /**
     * Enriches the response with the authentication cookie that contains the JWT contained in the given authentication.<br>
     *
     * @param response       The response to enrich with the authentication cookie
     * @param authentication The authentication that contains the JWT which sould be set as cookie
     */
    public void setAuthenticationCookie(HttpServletResponse response, Authentication authentication) {
        log.debug("CookieHandler.setAuthenticationCookie");
        if (authentication != null) {
            Object details = authentication.getDetails();
            if (details != null && details instanceof IJsonWebToken) {
                log.debug("Adding cookie with authorization token for user: {}", authentication.getPrincipal());
                String authToken = ((IJsonWebToken) details).getJwt();
                response.addCookie(createLoginCookie(authToken));
            } else {
                log.debug("Spring SecurityContext does not contain the IM AuthorizationToken");
                throw new AuthenticationCredentialsNotFoundException("Spring SecurityContext does not contain the IM AuthorizationToken. "
                        + "SecurityContextHolder.getContext().getAuthentication().getDetails() == " + details);
            }
        } else {
            log.debug("Spring SecurityContext does not contain the IM AuthorizationToken");
            throw new AuthenticationCredentialsNotFoundException("Spring SecurityContext does not contain the IM AuthorizationToken. "
                    + "SecurityContextHolder.getContext().getAuthentication() == null");
        }
    }

    private Cookie createLoginCookie(String authToken) {
        Cookie cookie = new Cookie(cookieProperties.getName(), authToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(MAX_AGE);
        return cookie;
    }

    /**
     * Returns the authentication cookie or null is none is contained in the given request.
     *
     * @param request The request to extract the authentication cookie from
     * @return The authentication cookie or null if none is found in the given request
     */
    public Cookie getAuthorizationCookie(HttpServletRequest request) {
        Cookie cookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            cookie = Arrays.stream(cookies)
                    .filter(c -> c.getName().equals(cookieProperties.getName()))
                    .findFirst()
                    .orElse(null);
        }
        return cookie;
    }
}

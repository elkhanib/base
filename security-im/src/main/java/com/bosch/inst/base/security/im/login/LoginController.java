package com.bosch.inst.base.security.im.login;

import com.bosch.im.spring.security.token.PermissionsAuthorizationToken;
import com.bosch.im.spring.security.token.TenantUserPasswordToken;
import com.bosch.im.spring.service.ImAuthenticationProviderService;
import com.bosch.inst.base.security.im.auth.Credentials;
import com.bosch.inst.base.security.im.auth.CredentialsProperties;
import com.bosch.inst.base.security.im.cookie.AuthorizationCookieHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Upon a login request via BasicAuth the servlet filters in im-spring-security will authenticate the user,
 * retrieve the AuthorizationToken and set this token in the Spring SecurityContext.<br>
 * This controller adds the AuthorizationToken as Cookie for the client.<br>
 * There's no logout endpoint since this is provided and handled by Spring Security (see {@link com.bosch.ds.plcs.base.security.configuration.servlet.SecurityConfiguration.DefaultWebSecurityConfigurerAdapter}).
 */
@Import({CredentialsProperties.class, AuthorizationCookieHandler.class})
@Slf4j
@RestController
public class LoginController {
    @Autowired
    private CredentialsProperties credentialsProperties;

    @Autowired
    private AuthorizationCookieHandler authorizationCookieHandler;

    @Autowired
    private ImAuthenticationProviderService authenticationProviderService;

    @RequestMapping(value = "login", method = GET)
    public void getLogin(HttpServletRequest request, HttpServletResponse response) {
        // If we've come so far, the user already has been authenticated via BasicAuth and retrieved an AuthToken
        // which is available via the Spring SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authorizationCookieHandler.setAuthenticationCookie(response, authentication);
    }

    @RequestMapping(value = "login", method = POST)
    public void login(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody
            Credentials credentials) {
        TenantUserPasswordToken token = new TenantUserPasswordToken(credentials.getUsername(), credentials.getPassword(), credentials.getTenant());
        Authentication authentication = authenticationProviderService.authenticate(token);
        authorizationCookieHandler.setAuthenticationCookie(response, authentication);

        response.addHeader(credentialsProperties.getHeader(), ((PermissionsAuthorizationToken) authentication).getDetails().getJwt());
    }
}

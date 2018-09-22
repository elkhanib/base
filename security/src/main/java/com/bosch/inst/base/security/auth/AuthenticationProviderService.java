package com.bosch.inst.base.security.auth;

import com.bosch.inst.base.security.service.IUserProviderService;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Authenticates a user against IoT Permissions and requests the proper auth.
 */
@Service
@Slf4j
public class AuthenticationProviderService implements AuthenticationProvider {

    private static final String CREDENTIALS_ERROR = "Invalid credentials";


    private Set<Class> supportedAuth = new HashSet<>();

    private String defaultRolePrefix = "ROLE_";

    @Autowired
    private IUserProviderService userProviderService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    public AuthenticationProviderService() {
        supportedAuth.add(StringAuthorizationToken.class);
        supportedAuth.add(UsernamePasswordAuthenticationToken.class);
    }


    @Override
    public Authentication authenticate(Authentication authentication) {
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return authenticateUsernamePassword((UsernamePasswordAuthenticationToken) authentication);
        }
        if (authentication instanceof StringAuthorizationToken) {
            return authenticateWithPermissions(authentication.getCredentials().toString());
        }
        throw new IllegalStateException("Unsupported Authentication object");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    private Authentication authenticateUsernamePassword(UsernamePasswordAuthenticationToken auth) {
        String username = (String) auth.getPrincipal();
        String password = (String) auth.getCredentials();
        if (!StringUtils.hasText(username)) {
            throw new BadCredentialsException(CREDENTIALS_ERROR);
        }
        if (!StringUtils.hasText(password)) {
            throw new BadCredentialsException(CREDENTIALS_ERROR);
        }

        return authenticateWithPermissions(username, password);
    }


    private Authentication authenticateWithPermissions(String authorizationTokenString) {
        try { // parse the token.
            return getAuthentication(authorizationTokenString);
        } catch (Exception e) {
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }

    private Authentication authenticateWithPermissions(String username, String password) {
        try {// authenticate the username and password.
            return getAuthentication(username, password);
        } catch (Exception e) {
            log.warn("User {} found but password does not match!", username);
            throw new BadCredentialsException(CREDENTIALS_ERROR, e);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String username, String password) {
        try {
            UserDetails userDetails = userProviderService.authenticate(username, password);
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage());
            throw new BadCredentialsException(CREDENTIALS_ERROR, e);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecret())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage());
            throw new BadCredentialsException(CREDENTIALS_ERROR, e);
        }
    }
}

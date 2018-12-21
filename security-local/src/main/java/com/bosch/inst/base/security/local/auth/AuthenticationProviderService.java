package com.bosch.inst.base.security.local.auth;

import com.bosch.inst.base.security.local.service.ISecurityProvider;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Authenticates a user against IoT Permissions and requests the proper auth.
 */
@Profile("security-local")
@Service
@Slf4j
public class AuthenticationProviderService implements AuthenticationProvider {
    @Autowired
    private ISecurityProvider securityProvider;

    private static final String CREDENTIALS_ERROR = "Invalid credentials";


    private Set<Class> supportedAuth = new HashSet<>();

    private String defaultRolePrefix = "ROLE_";

    @Autowired
    private ISecurityProvider userProviderService;

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
        return supportedAuth.contains(aClass);
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
            log.warn(e.getLocalizedMessage());
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }

    private Authentication authenticateWithPermissions(String username, String password) {
        try { // authenticate the username and password.
            return getAuthentication(username, password);
        } catch (BadCredentialsException e) {
            log.warn(e.getLocalizedMessage(), username);
            throw new BadCredentialsException(CREDENTIALS_ERROR, e);
        } catch (NoSuchElementException e) {
            log.warn("User {} can not be found!", username);
            throw new BadCredentialsException(CREDENTIALS_ERROR, e);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String username, String password) {

        if (!userProviderService.validate(username, password)) {
            throw new BadCredentialsException(CREDENTIALS_ERROR);
        } else {
            UserDetails userDetails = userProviderService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        // parse the token.
        String user = Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return new UsernamePasswordAuthenticationToken(user, null, securityProvider.loadUserByUsername(user).getAuthorities());
    }

}

package com.bosch.inst.base.security.service;

import com.bosch.inst.base.security.authorization.JwtProperties;
import com.bosch.inst.base.security.authorization.StringAuthorizationToken;
import io.jsonwebtoken.Jwts;
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
 * Authenticates a user against IoT Permissions and requests the proper authorization.
 */
@Service
public class ImAuthenticationProviderService implements AuthenticationProvider {

    private static final String CREDENTIALS_ERROR = "Invalid credentials";


    private Set<Class> supportedAuth = new HashSet<>();

    private String defaultRolePrefix = "ROLE_";

    @Autowired
    private IUserProviderService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    public ImAuthenticationProviderService() {
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
        try {
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        } catch (Exception e) {
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        // parse the token.
        String user = Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    }
}

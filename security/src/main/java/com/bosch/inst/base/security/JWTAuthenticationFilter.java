package com.bosch.inst.base.security;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
    private String jwtHeader;
    private String jwtSecret;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, String jwtHeader, String jwtSecret) {
        super(authenticationManager);
        this.jwtHeader = jwtHeader;
        this.jwtSecret = jwtSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String headerToken = request.getHeader(jwtHeader);
        String cookieToken = getCookieValue(request, jwtHeader);

        String token = "";

        if (headerToken != null) {
            token = request.getHeader(jwtHeader);
        } else if (cookieToken != null) {
            token = getCookieValue(request, jwtHeader);
        } else {
            chain.doFilter(request, response);
            return;
        }


        UsernamePasswordAuthenticationToken authentication = getAuthentication(request, token);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);

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

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }

}

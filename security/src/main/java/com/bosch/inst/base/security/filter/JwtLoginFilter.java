package com.bosch.inst.base.security.filter;

import com.bosch.inst.base.security.auth.JwtUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger LOG = LoggerFactory.getLogger(JwtLoginFilter.class);

    private String jwtHeader;
    private String jwtSecret;
    private Long jwtExpire;
    private String cookieName;

    private AuthenticationManager authenticationManager;


    public JwtLoginFilter(String jwtHeader, String jwtSecret, Long jwtExpire, String cookieName, AuthenticationManager authenticationManager) {
        this.jwtHeader = jwtHeader;
        this.jwtSecret = jwtSecret;
        this.jwtExpire = jwtExpire;
        this.cookieName = cookieName;
        this.authenticationManager = authenticationManager;
    }

    // 接收并解析用户凭证
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            JwtUser user = new ObjectMapper()
                    .readValue(req.getInputStream(), JwtUser.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 用户成功登录后，这个方法会被调用，我们在这个方法里生成token
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        String token = Jwts.builder()
                .setSubject(((User) auth.getPrincipal()).getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpire))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
        res.addHeader(jwtHeader, token);

        //构造Cookie对象
        //添加到Cookie中
        Cookie c = new Cookie(cookieName, token);
        //设置过期时间
        c.setMaxAge(jwtExpire.intValue());
        //存储
        res.addCookie(c);
    }
}

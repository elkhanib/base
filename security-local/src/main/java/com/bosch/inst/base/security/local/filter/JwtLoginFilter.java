package com.bosch.inst.base.security.local.filter;

import com.bosch.inst.base.security.local.auth.AuthenticationProperties;
import com.bosch.inst.base.security.local.auth.JwtProperties;
import com.bosch.inst.base.security.local.auth.JwtUser;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger LOG = LoggerFactory.getLogger(JwtLoginFilter.class);

    private JwtProperties jwtProperties;
    private AuthenticationProperties authProperties;
    private AuthenticationManager authenticationManager;


    public JwtLoginFilter(JwtProperties jwtProperties, AuthenticationProperties authProperties, AuthenticationManager authenticationManager) {
        this.jwtProperties = jwtProperties;
        this.authProperties = authProperties;
        this.authenticationManager = authenticationManager;
    }

    // 接收并解析用户凭证
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            InputStream inputStream = req.getInputStream();
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = inputStream.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            JwtUser user = new Gson().fromJson(out.toString(), JwtUser.class);

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
                                            Authentication auth) {

        String token = Jwts.builder()
                .setSubject(((User) auth.getPrincipal()).getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpire()))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
                .compact();
        res.addHeader(jwtProperties.getHeader(), token);

        //构造Cookie对象
        //添加到Cookie中
        Cookie c = new Cookie(authProperties.getCookie().getName(), token);
        //设置过期时间
        c.setMaxAge(authProperties.getCookie().getMaxAge());
        c.setPath("/");
        //存储
        res.addCookie(c);
        res.setStatus(HttpStatus.NO_CONTENT.value());
    }
}

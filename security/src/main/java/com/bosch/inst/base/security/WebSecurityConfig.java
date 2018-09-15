package com.bosch.inst.base.security;

import com.bosch.inst.base.security.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${jwt.header:X-Access-Token}")
    public String JWT_HEADER;

    @Value("${jwt.secret:MyJwtSecret}")
    public String JWT_SECRET;

    @Value("${jwt.expire:7200000}")
    public Long JWT_EXPIRE;

    @Autowired
    private IUserService userService;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/api/v1/car-ads-account/user/password/reset", "/api/v1/car-ads-account/register").permitAll()
                .anyRequest().authenticated();


        http.addFilter(new JWTLoginFilter(authenticationManager(), JWT_HEADER, JWT_SECRET, JWT_EXPIRE));
        http.addFilter(new JWTAuthenticationFilter(authenticationManager(), JWT_HEADER, JWT_SECRET));


        http.logout()
                .deleteCookies(JWT_HEADER)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));

        // Return the 'WWW-Authenticate: Basic' header in case of missing credentials
        http.httpBasic();

        // Hint:
        // We disable csrf since we are running stateless REST services.
        // Instead of the Synchronized Token Pattern we check for the presence of a custom request header.
        // -> Only JavaScript can be used to add a custom header, and only within its origin.
        // -> See https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)_Prevention_Cheat_Sheet
        // Section 'Protecting REST Services: Use of Custom Request Headers'
        http.csrf().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.userDetailsService(userService).passwordEncoder(encoder);
    }
}

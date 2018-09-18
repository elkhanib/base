package com.bosch.inst.base.security;

import com.bosch.inst.base.security.authorization.AuthProperties;
import com.bosch.inst.base.security.authorization.JwtProperties;
import com.bosch.inst.base.security.filter.JwtLoginFilter;
import com.bosch.inst.base.security.filter.XRequestedHeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
public class WebSecurityConfigurationAdapter extends ImWebSecurityConfigurerAdapter {
    @Autowired
    private AuthProperties authProperties;

    @Autowired
    private JwtProperties jwtProperties;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/", "/csrf", "/swagger-ui.html", "/webjars/springfox-swagger-ui/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
                .anyRequest().authenticated();


        http.addFilter(new JwtLoginFilter(jwtProperties.getHeader(), jwtProperties.getSecret(), jwtProperties.getExpire(), authProperties.getCookie().getName(), authenticationManager()));

        // Don't use sessions for stateless REST interfaces
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

//        http.addFilterBefore(new EnforceSecureLoginFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(getTokenAuthFilter("/**"), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(getBasicAuthFilter("/**"), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(getCookieAuthFilter("/**"), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new XRequestedHeaderFilter(authProperties), UsernamePasswordAuthenticationFilter.class);

        http.logout()
                .deleteCookies(authProperties.getCookie().getName())
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

}

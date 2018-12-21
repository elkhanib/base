package com.bosch.inst.base.security.local;

import com.bosch.inst.base.security.local.auth.AuthenticationProperties;
import com.bosch.inst.base.security.local.auth.JwtProperties;
import com.bosch.inst.base.security.local.filter.CorsFilter;
import com.bosch.inst.base.security.local.filter.JwtLoginFilter;
import com.bosch.inst.base.security.local.filter.XRequestedHeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Profile("security-local")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurationAdapter extends AbstractWebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationProperties authProperties;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/", "/csrf", "/error", "/api/users/signup", "/swagger-ui.html", "/doc.html", "/webjars/springfox-swagger-ui/**", "/webjars/bycdao-ui/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
                .antMatchers("/actuator/**").hasAnyAuthority("ACTUATOR")
                .anyRequest().authenticated();

        http.addFilter(new JwtLoginFilter(jwtProperties, authProperties, authenticationManager()));

        // Don't use sessions for stateless REST interfaces
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // http.addFilterBefore(new EnforceSecureLoginFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new CorsFilter(), UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(getTokenAuthFilter("/**"), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(getBasicAuthFilter("/**"), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(getCookieAuthFilter("/**"), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new XRequestedHeaderFilter(authProperties), UsernamePasswordAuthenticationFilter.class);

        http.logout()
                .deleteCookies(authProperties.getCookie().getName())
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));

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

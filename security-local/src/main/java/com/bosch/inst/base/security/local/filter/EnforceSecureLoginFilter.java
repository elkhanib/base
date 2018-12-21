package com.bosch.inst.base.security.local.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter enforces the user to call the applications only via https.
 * Because Cloudfoundry sets an extra HTTP header named x-forwarded-proto, whose value will be either http OR https.
 * We have to distinguish between the secure and unsecure login, and have to block the insecure HTTP connection.
 * If ServletRequest.isSecure() is false, this filter retruns with Responsecode 403 forbidden and gives a hint only to connect via https.
 */
public class EnforceSecureLoginFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        boolean secureLogin = request.isSecure();
        if (!secureLogin) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Use HTTPS in order to connect to the API.");
        } else {
            chain.doFilter(request, response);
        }
    }

}

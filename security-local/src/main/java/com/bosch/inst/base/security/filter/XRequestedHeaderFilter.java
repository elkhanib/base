package com.bosch.inst.base.security.filter;

import com.bosch.inst.base.security.auth.AuthenticationProperties;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * This is a filter to prevent CSRF attacks by checking the xRequestedHeader Header field.
 * Explanation:
 * When the browser makes an AJAX request a special header field is set the X-Requested-With.
 * This field is a non-standard header field, but it is used in many JavaScript libraries.
 * On the server side in this XRequestedHeaderFilter class, we validate the request to make sure the header exists in the request.
 * If it does not, the request is rejected.
 * This works because of the support of the Same-origin Policy (SOP)
 * To add this special header to a request within the context of the browser, the attacker needs to use XMLHttpRequest.
 * But because of SOP, you can not by default send an AJAX request to a third-party domain using XMLHttpRequest.
 */
public class XRequestedHeaderFilter extends GenericFilterBean {

    private static final String X_REQUESTED_WITH = "X-Requested-With";
    private static final String XMLHTTP_REQUEST = "XMLHttpRequest";
    private static final String METHOD_GET = "GET";
    private String cookieName;

    public XRequestedHeaderFilter(AuthenticationProperties properties) {
        cookieName = properties.getCookie().getName();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (METHOD_GET.equals(request.getMethod())) {
            // if it's a GET request, try next filter, XRequestedHeader check is not needed.
            chain.doFilter(request, response);
            return;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            // no cookie is set, try next filter, XRequestedHeader check is not needed.
            chain.doFilter(request, response);
            return;
        }
        Cookie cookie = Arrays.stream(cookies).filter(c -> c.getName().equals(cookieName)).findFirst().orElse(null);
        if (cookie == null) {
            //no cookie with name 'cookieName' is set, try next filter, XRequestedHeader check is not needed.
            chain.doFilter(request, response);
            return;
        }
        //cookie with name 'cookieName' is set, check if X-Requested-With set correct.
        String xRequestedHeader = request.getHeader(X_REQUESTED_WITH);
        if (xRequestedHeader != null && XMLHTTP_REQUEST.equals(xRequestedHeader)) {
            chain.doFilter(request, response);
            return;
        }
        //cookie with name 'cookieName' is set, but X-Requested-With field is not set correct, maybe CSRF attack, return with 401
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}


package com.bosch.inst.base.rest;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ServletContainerCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

	@Override
	public void customize(ConfigurableServletWebServerFactory factory) {
		factory.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/error/400"));
		factory.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, "/error/401"));
		factory.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/error/403"));
		factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"));
		factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"));
		factory.addErrorPages(new ErrorPage(Throwable.class, "/error/500"));
		factory.addErrorPages(new ErrorPage(HttpStatus.SERVICE_UNAVAILABLE, "/error/503"));
	}
}

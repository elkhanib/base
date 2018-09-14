package com.bosch.inst.esurvey.base.querydsl.web;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@AllArgsConstructor
public class LinkParameterArgumentResolver implements HandlerMethodArgumentResolver {
    private final ApplicationContext appContext;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Link.class.equals(parameter.getParameterType());
    }

    @Override
    public Link resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Method method = parameter.getMethod();
        String sort = webRequest.getParameter("sort");
        String search = webRequest.getParameter("search");

        UriComponentsBuilder builder = linkTo(method, (Object[]) method.getParameters()).toUriComponentsBuilder();

        if (sort != null) {
            builder.query("sort=" + sort);
        }

        if (search != null) {
            builder.query("search=" + search);
        }

        UriComponents uriComponents = builder.build();
        String uri = uriComponents.encode().toUriString();
        return new Link(uri).withSelfRel();
    }
}

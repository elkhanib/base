package com.bosch.inst.esurvey.base.querydsl.web;

import com.bosch.inst.esurvey.base.querydsl.QueryDsl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class DslParameterArgumentResolver implements HandlerMethodArgumentResolver {

    private final ApplicationContext appContext;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (QueryDsl.class.equals(parameter.getParameterType()) && parameter.hasParameterAnnotation(DslCustomizer.class)) {
            return true;
        }

        if (QueryDsl.class.equals(parameter.getParameterType())) {
            throw new IllegalArgumentException(String.format("Parameter at position %s must be annotated with %s",
                    parameter.getParameterIndex(), DslCustomizer.class.getName()));
        }

        return false;
    }

    @Override
    public QueryDsl resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //to do cache querydsl instance
        QueryDsl.Customizer bean = appContext.getBean(parameter.getParameterAnnotation(DslCustomizer.class).value());
        QueryDsl queryDsl = new QueryDsl();
        bean.configureFilter(queryDsl);
        bean.configureSorting(queryDsl);
        return queryDsl;
    }
}

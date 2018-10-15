package com.bosch.inst.base.querydsl.web;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.QuerydslWebConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Import(QuerydslWebConfiguration.class)
public class QueryDslConfiguration implements WebMvcConfigurer, ApplicationContextAware {
    private ApplicationContext context;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(0, new DslParameterArgumentResolver(context));
        argumentResolvers.add(0, new SortParameterArgumentResolver(context));
        argumentResolvers.add(1, new LinkParameterArgumentResolver(context));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}

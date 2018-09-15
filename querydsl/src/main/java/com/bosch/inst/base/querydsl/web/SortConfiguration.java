package com.bosch.inst.base.querydsl.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.HateoasAwareSpringDataWebConfiguration;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@Import(HateoasAwareSpringDataWebConfiguration.class)
public class SortConfiguration {

    private class PlcsSortHandlerMethodArgumentResolver extends HateoasSortHandlerMethodArgumentResolver {

        @Override
        public void enhance(UriComponentsBuilder builder, MethodParameter parameter, Object value) {
            Object sort = value;
            if (value instanceof PlcsSort) {
                sort = ((PlcsSort) value).getOriginalSort();
            }
            super.enhance(builder, parameter, sort);
        }
    }

    @Bean
    public HateoasSortHandlerMethodArgumentResolver sortResolver() {
        return new PlcsSortHandlerMethodArgumentResolver();
    }

}

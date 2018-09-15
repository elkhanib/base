package com.bosch.inst.base.querydsl.web;

import com.bosch.inst.base.querydsl.QueryDsl;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class SortParameterArgumentResolver extends HateoasPageableHandlerMethodArgumentResolver {

    private final ApplicationContext appContext;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.equals(parameter.getParameterType()) && parameter.hasParameterAnnotation(DslCustomizer.class);
    }

    @Override
    public Pageable resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Pageable pageable = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        if (pageable.getSort() == null) {
            return pageable;
        }

        //to do cache querydsl instance
        QueryDsl.Customizer bean = appContext.getBean(parameter.getParameterAnnotation(DslCustomizer.class).value());
        QueryDsl queryDsl = new QueryDsl();
        bean.configureSorting(queryDsl);

        List<OrderSpecifier> orderSpecifiers =
                StreamSupport.stream(pageable.getSort().spliterator(), false)
                        .map(order -> {
                            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                            return queryDsl.processSort(order.getProperty(), direction);
                        }).collect(Collectors.toList());

        QSort sort = new PlcsSort(pageable.getSort(), orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]));
        return new QPageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}

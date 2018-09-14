package com.bosch.inst.esurvey.base.querydsl;

import com.querydsl.core.types.Path;

public interface Filtering {
    <T extends Path<S>, S> QueryDsl.Binding<T, S> createFilter(T propertyInQClass);

    <T extends Path<S>, S> QueryDsl.Binding<T, S> createFilter(T propertyInQClass, String dtoProperty);
}

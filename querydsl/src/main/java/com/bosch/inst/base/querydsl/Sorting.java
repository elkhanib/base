package com.bosch.inst.base.querydsl;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;

public interface Sorting {
    <T extends ComparableExpressionBase<S> & Path<S>, S extends Comparable> void createSort(T propertyInQClass);

    <T extends ComparableExpressionBase<S> & Path<S>, S extends Comparable> void createSort(T propertyInQClass, String dtoProperty);
}

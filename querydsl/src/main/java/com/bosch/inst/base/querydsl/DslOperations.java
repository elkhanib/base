package com.bosch.inst.base.querydsl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringPath;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;

import java.time.Instant;

public final class DslOperations {

    public static final ComparisonOperator EQUAL = new ComparisonOperator("==");
    public static final ComparisonOperator NOT_EQUAL = new ComparisonOperator("!=");
    public static final ComparisonOperator GREATER_THAN = new ComparisonOperator("=gt=", ">");
    public static final ComparisonOperator GREATER_THAN_OR_EQUAL = new ComparisonOperator("=ge=", ">=");
    public static final ComparisonOperator LESS_THAN = new ComparisonOperator("=lt=", "<");
    public static final ComparisonOperator LESS_THAN_OR_EQUAL = new ComparisonOperator("=le=", "<=");
    public static final ComparisonOperator IN = new ComparisonOperator("=in=", true);
    public static final ComparisonOperator NOT_IN = new ComparisonOperator("=out=", true);
    public static final ComparisonOperator CONTAINS = new ComparisonOperator("=co=");
    public static final ComparisonOperator IS = new ComparisonOperator("=is=");
    public static final ComparisonOperator IS_EMPTY = new ComparisonOperator("=isempty=");
    public static final ComparisonOperator AVAILABLE = new ComparisonOperator("=available=");

    public static <T> Predicate eq(SimpleExpression<T> path, T value) {
        return path.eq(value);
    }

    public static Predicate eqIc(StringPath path, String value) {
        return path.equalsIgnoreCase(value);
    }

    public static Predicate containsIc(StringPath path, String value) {
        return path.containsIgnoreCase(value);
    }

    public static <T extends Number & Comparable<T>> Predicate gt(NumberExpression<T> path, T value) {
        return path.gt(value);
    }

    public static <T extends Number & Comparable<T>> Predicate goe(NumberExpression<T> path, T value) {
        return path.goe(value);
    }

    public static <T extends Number & Comparable<T>> Predicate lt(NumberExpression<T> path, T value) {
        return path.lt(value);
    }

    public static <T extends Number & Comparable<T>> Predicate loe(NumberExpression<T> path, T value) {
        return path.loe(value);
    }

    public static Predicate gt(DateTimePath<Instant> path, Instant instant) {
        return path.gt(instant);
    }

    public static Predicate goe(DateTimePath<Instant> path, Instant instant) {
        return path.goe(instant);
    }

    public static Predicate lt(DateTimePath<Instant> path, Instant instant) {
        return path.lt(instant);
    }

    public static Predicate loe(DateTimePath<Instant> path, Instant instant) {
        return path.loe(instant);
    }
}

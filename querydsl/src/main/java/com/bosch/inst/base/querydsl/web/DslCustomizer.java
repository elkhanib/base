package com.bosch.inst.base.querydsl.web;

import com.bosch.inst.base.querydsl.QueryDsl;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DslCustomizer {
    Class<? extends QueryDsl.Customizer> value();
}
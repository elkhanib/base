package com.bosch.inst.esurvey.base.querydsl;

public interface DslConverter<T> {

    T convert(String source);
}

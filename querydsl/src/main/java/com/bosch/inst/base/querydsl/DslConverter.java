package com.bosch.inst.base.querydsl;

public interface DslConverter<T> {

    T convert(String source);
}

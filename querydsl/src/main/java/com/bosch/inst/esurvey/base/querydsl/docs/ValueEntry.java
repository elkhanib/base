package com.bosch.inst.esurvey.base.querydsl.docs;

import lombok.Value;

@Value
public class ValueEntry implements Entry {
    private final String selector;
    private final Iterable<String> values;

    @Override
    public String selector() {
        return selector;
    }

    public Iterable<String> values() {
        return values;
    }
}

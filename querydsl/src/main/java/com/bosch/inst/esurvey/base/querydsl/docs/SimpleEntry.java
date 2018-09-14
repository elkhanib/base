package com.bosch.inst.esurvey.base.querydsl.docs;

import lombok.Value;

@Value
public class SimpleEntry implements Entry {
    private final String selector;

    @Override
    public String selector() {
        return selector;
    }
}

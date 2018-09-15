package com.bosch.inst.base.rest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorDef {
    SUCCESS(0, "Success"),
    UNKNOWN_EXCEPTION(-1, "Unknown Exception"),
    NO_SUCH_ELEMENT_EXCEPTION(10001, "No Such Element Exception"),
    ILLEGAL_ARGUMENT_EXCEPTION(10002, "Illegal Argument Exception"),
    NUMBER_FORMAT_EXCEPTION(10003, "Number Format Exception"),
    ILLEGAL_STATE_EXCEPTION(10004, "Illegal State Exception"),
    DATA_INTEGRITY_VIOLATION_EXCEPTION(10005, "Data Integrity Violation Exception"),
    UNKNOWN_REQUEST_PARAMETER_EXCEPTION(10006, "Unknown Request Parameter Exception"),
    ;

    private final int value;
    private final String reasonPhrase;
}

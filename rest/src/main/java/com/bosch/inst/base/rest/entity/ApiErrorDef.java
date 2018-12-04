package com.bosch.inst.base.rest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiErrorDef implements BaseErrorDef {
    SUCCESS(0, "Success"),
    UNKNOWN_EXCEPTION(-1, "Unknown Error"),
    NO_SUCH_ELEMENT_EXCEPTION(1001, "No Such Element"),
    ILLEGAL_ARGUMENT_EXCEPTION(1002, "Illegal Argument"),
    NUMBER_FORMAT_EXCEPTION(1003, "Number Format"),
    ILLEGAL_STATE_EXCEPTION(1004, "Illegal State"),
    DATA_INTEGRITY_VIOLATION_EXCEPTION(1005, "Data Integrity Violation"),
    UNKNOWN_REQUEST_PARAMETER_EXCEPTION(1006, "Unknown Request Parameter"),
    CLIENT_ABORT_EXCEPTION(1007, "Client Abort"),
    BAD_CREDENTIALS_EXCEPTION(1008, "Bad Credentials"),
    ACCESS_DENIED_EXCEPTION(1009, "Access Denied"),
    ;

    private final int value;
    private final String reasonPhrase;
}

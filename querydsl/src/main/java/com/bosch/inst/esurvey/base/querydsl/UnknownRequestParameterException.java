package com.bosch.inst.esurvey.base.querydsl;

public class UnknownRequestParameterException extends RuntimeException {
    UnknownRequestParameterException(String message) {
        super(message);
    }

    UnknownRequestParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}

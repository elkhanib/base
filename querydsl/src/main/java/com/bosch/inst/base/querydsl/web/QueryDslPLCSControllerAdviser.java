package com.bosch.inst.base.querydsl.web;

import com.bosch.inst.base.querydsl.UnknownRequestParameterException;
import com.bosch.inst.base.rest.entity.ErrorDef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The QueryDslPLCSControllerAdviser is the adviser
 * which should only handle errors during filter / searching with QueryDsl API.
 */
@Slf4j
@ControllerAdvice
@Order // Let the project specific advisers get a chance to step in
public class QueryDslPLCSControllerAdviser {

    public static final String LOGREF_ERROR = "error";

    @ResponseBody
    @ExceptionHandler(UnknownRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndError unknownRequestParameterException(UnknownRequestParameterException ex) {
        log.warn(ex.getLocalizedMessage());
        return new VndError(String.valueOf(ErrorDef.UNKNOWN_REQUEST_PARAMETER_EXCEPTION.getValue()), ex.getMessage());
    }
}
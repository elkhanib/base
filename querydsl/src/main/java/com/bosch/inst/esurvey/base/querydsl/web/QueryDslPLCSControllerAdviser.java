package com.bosch.inst.esurvey.base.querydsl.web;

import com.bosch.inst.esurvey.base.querydsl.UnknownRequestParameterException;
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
        return new VndError(LOGREF_ERROR, ex.getMessage());
    }
}
package com.bosch.inst.base.querydsl.web;

import com.bosch.inst.base.querydsl.UnknownRequestParameterException;
import com.bosch.inst.base.rest.entity.ApiError;
import com.bosch.inst.base.rest.entity.ApiErrorDef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * The QueryDslPLCSControllerAdviser is the adviser
 * which should only handle errors during filter / searching with QueryDsl API.
 */
@Slf4j
@ControllerAdvice
@Order // Let the project specific advisers get a chance to step in
public class QueryDslControllerAdviser {
    @ResponseBody
    @ExceptionHandler(UnknownRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Object> unknownRequestParameterException(HttpServletRequest request, UnknownRequestParameterException ex) {
        log.warn(ApiErrorDef.UNKNOWN_REQUEST_PARAMETER_EXCEPTION.getReasonPhrase(), ex.getLocalizedMessage());

        ApiError apiError = new ApiError(ApiErrorDef.UNKNOWN_REQUEST_PARAMETER_EXCEPTION, request, ex);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
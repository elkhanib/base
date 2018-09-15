package com.bosch.inst.esurvey.base.rest;

import com.bosch.inst.esurvey.base.rest.entity.ErrorDef;
import com.bosch.inst.esurvey.base.rest.entity.ErrorMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

/**
 * The ControllerAdviser is the standard advisor to be aware of errors and treat them accordingly.
 */
@Slf4j
@ControllerAdvice
@Order // Let the project specific advisers get a chance to step in
public class ControllerAdviser {
    @ResponseBody
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorMsg noSuchElementExceptionHandler(NoSuchElementException ex) {
        log.warn("Error", ex);
        return new ErrorMsg(ErrorDef.NO_SUCH_ELEMENT_EXCEPTION.getValue(), ErrorDef.NO_SUCH_ELEMENT_EXCEPTION.getReasonPhrase() + ": " + ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMsg illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        log.warn("Error", ex);
        return new ErrorMsg(ErrorDef.ILLEGAL_ARGUMENT_EXCEPTION.getValue(), ErrorDef.ILLEGAL_ARGUMENT_EXCEPTION.getReasonPhrase() + ": " + ex.getMessage());
    }

    /**
     * Used to catch NumberFormatExceptions, which might occur while
     * parsing String parameter "version" to int for optimistic locking (ETag)
     *
     * @param ex
     * @return ErrorMsg
     */
    @ResponseBody
    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMsg dataAccessExceptionHandler(NumberFormatException ex) {
        log.warn("Error", ex);
        return new ErrorMsg(ErrorDef.NUMBER_FORMAT_EXCEPTION.getValue(), ErrorDef.NUMBER_FORMAT_EXCEPTION.getReasonPhrase() + ": " + ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    ErrorMsg illegalStateExceptionHandler(IllegalStateException ex) {
        log.warn("Error", ex);
        return new ErrorMsg(ErrorDef.ILLEGAL_STATE_EXCEPTION.getValue(), ErrorDef.ILLEGAL_STATE_EXCEPTION.getReasonPhrase() + ": " + ex.getMessage());
    }

}
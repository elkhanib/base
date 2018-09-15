package com.bosch.inst.esurvey.base.rest;

import com.bosch.inst.esurvey.base.rest.entity.ErrorDef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ConcurrentModificationException;
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
    VndErrors.VndError noSuchElementExceptionHandler(NoSuchElementException ex) {
        log.warn(ErrorDef.NO_SUCH_ELEMENT_EXCEPTION.getReasonPhrase(), ex.getLocalizedMessage());
        return new VndErrors.VndError(String.valueOf(ErrorDef.NO_SUCH_ELEMENT_EXCEPTION.getValue()), ErrorDef.NO_SUCH_ELEMENT_EXCEPTION.getReasonPhrase() + ": " + ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors.VndError illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        log.warn(ErrorDef.ILLEGAL_ARGUMENT_EXCEPTION.getReasonPhrase(), ex.getLocalizedMessage());
        return new VndErrors.VndError(String.valueOf(ErrorDef.ILLEGAL_ARGUMENT_EXCEPTION.getValue()), ErrorDef.ILLEGAL_ARGUMENT_EXCEPTION.getReasonPhrase() + ": " + ex.getMessage());
    }

    /**
     * Used to catch NumberFormatExceptions, which might occur while
     * parsing String parameter "version" to int for optimistic locking (ETag)
     *
     * @param ex
     * @return VndError
     */
    @ResponseBody
    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors.VndError dataAccessExceptionHandler(NumberFormatException ex) {
        String message = "NumberFormatException occurred: " + ex.getMessage();
        log.warn(message);
        return new VndErrors.VndError(String.valueOf(ErrorDef.NUMBER_FORMAT_EXCEPTION.getValue()), message);
    }

    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    VndErrors.VndError illegalStateExceptionHandler(IllegalStateException ex) {
        log.warn(ErrorDef.ILLEGAL_STATE_EXCEPTION.getReasonPhrase(), ex);
        return new VndErrors.VndError(String.valueOf(ErrorDef.ILLEGAL_STATE_EXCEPTION.getValue()), ErrorDef.ILLEGAL_STATE_EXCEPTION.getReasonPhrase() + ": " + ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({DataIntegrityViolationException.class, ConcurrentModificationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    VndErrors.VndError conflictException(Exception ex) {
        log.warn(ex.getLocalizedMessage());
        return new VndErrors.VndError(String.valueOf(ErrorDef.DATA_INTEGRITY_VIOLATION_EXCEPTION.getValue()), ex.getMessage());
    }

}
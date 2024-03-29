package com.bosch.inst.base.rest;

import com.bosch.inst.base.rest.entity.ApiError;
import com.bosch.inst.base.rest.entity.ApiErrorDef;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * The ControllerAdviser is the standard advisor to be aware of errors and treat them accordingly.
 */
@Slf4j
@ControllerAdvice
@Order // Let the project specific advisers get a chance to step in
public class ControllerAdviser extends ResponseEntityExceptionHandler {
    @ResponseBody
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseEntity<Object> noSuchElementExceptionHandler(HttpServletRequest request, NoSuchElementException ex) {
        log.warn(ApiErrorDef.NO_SUCH_ELEMENT_EXCEPTION.getReasonPhrase());
        log.warn(ex.getLocalizedMessage());

        ApiError apiError = new ApiError(ApiErrorDef.NO_SUCH_ELEMENT_EXCEPTION, request, ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<Object> accessDeniedExceptionHandler(HttpServletRequest request, AccessDeniedException ex) {
        log.warn(ApiErrorDef.ACCESS_DENIED_EXCEPTION.getReasonPhrase());
        log.warn(ex.getLocalizedMessage());

        ApiError apiError = new ApiError(ApiErrorDef.ACCESS_DENIED_EXCEPTION, request, ex);
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ResponseBody
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<Object> badCredentialsExceptionHandler(HttpServletRequest request, BadCredentialsException ex) {
        log.warn(ApiErrorDef.BAD_CREDENTIALS_EXCEPTION.getReasonPhrase());
        log.warn(ex.getLocalizedMessage());

        ApiError apiError = new ApiError(ApiErrorDef.BAD_CREDENTIALS_EXCEPTION, request, ex);
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Object> illegalArgumentExceptionHandler(HttpServletRequest request, IllegalArgumentException ex) {
        log.warn(ApiErrorDef.ILLEGAL_ARGUMENT_EXCEPTION.getReasonPhrase());
        log.warn(ex.getLocalizedMessage());

        ApiError apiError = new ApiError(ApiErrorDef.ILLEGAL_ARGUMENT_EXCEPTION, request, ex);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
//        return new ResponseEntity<Object>(String.valueOf(ApiErrorDef.ILLEGAL_ARGUMENT_EXCEPTION.getValue()), ApiErrorDef.ILLEGAL_ARGUMENT_EXCEPTION.getReasonPhrase() + ": " + ex.getMessage());
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
    ResponseEntity<Object> dataAccessExceptionHandler(HttpServletRequest request, NumberFormatException ex) {
        String message = "NumberFormatException occurred: " + ex.getMessage();
        log.warn(message);
//        return new ResponseEntity<Object>(String.valueOf(ApiErrorDef.NUMBER_FORMAT_EXCEPTION.getValue()), message);
        ApiError apiError = new ApiError(ApiErrorDef.NUMBER_FORMAT_EXCEPTION, request, ex);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    ResponseEntity<Object> illegalStateExceptionHandler(HttpServletRequest request, IllegalStateException ex) {
        log.warn(ApiErrorDef.ILLEGAL_STATE_EXCEPTION.getReasonPhrase(), ex);

        ApiError apiError = new ApiError(ApiErrorDef.ILLEGAL_STATE_EXCEPTION, request, ex);
        return new ResponseEntity<>(apiError, HttpStatus.PRECONDITION_FAILED);
//        return new ResponseEntity<Object>(String.valueOf(ApiErrorDef.ILLEGAL_STATE_EXCEPTION.getValue()), ApiErrorDef.ILLEGAL_STATE_EXCEPTION.getReasonPhrase() + ": " + ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({DataIntegrityViolationException.class, ConcurrentModificationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    ResponseEntity<Object> conflictException(HttpServletRequest request, Exception ex) {
        log.warn(ex.getLocalizedMessage());

        ApiError apiError = new ApiError(ApiErrorDef.DATA_INTEGRITY_VIOLATION_EXCEPTION, request, ex);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
//        return new ResponseEntity<Object>(String.valueOf(ApiErrorDef.DATA_INTEGRITY_VIOLATION_EXCEPTION.getValue()), ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(ClientAbortException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Object> clientAbortException(HttpServletRequest request, Exception ex) {
        log.warn(ex.getLocalizedMessage());

        ApiError apiError = new ApiError(ApiErrorDef.CLIENT_ABORT_EXCEPTION, request, ex);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

}
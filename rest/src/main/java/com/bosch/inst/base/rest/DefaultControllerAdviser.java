package com.bosch.inst.base.rest;

import com.bosch.inst.base.rest.entity.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * The DefaultControllerAdvicer is an Advicer which extends the {@link ResponseEntityExceptionHandler}
 * and thereby handles the standard Spring MVC exceptions.
 * As well:
 * Override the handleMethodArgumentNotValid methode to handle the {@link MethodArgumentNotValidException}
 * Handles all other {@link Exception} and Response with 500 Internal Server Error
 */
@Slf4j
@ControllerAdvice
@Order // Let the project specific advisers get a chance to step in
public class DefaultControllerAdviser extends ResponseEntityExceptionHandler {

    public static final String LOGREF_ERROR = "error";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn(ex.getLocalizedMessage());
        if (ex.getBindingResult() != null && ex.getBindingResult().getAllErrors() != null && !ex.getBindingResult()
                .getAllErrors()
                .isEmpty()) {
            ObjectError error = ex.getBindingResult().getAllErrors().get(0);
            return handleExceptionInternal(ex, new VndErrors.VndError(LOGREF_ERROR, error.getDefaultMessage()), headers,
                    status, request);
        }
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @SuppressWarnings("squid:S00112")
    ResponseEntity<Object> exceptionHandler(HttpServletRequest request, Exception ex) throws Exception {
        log.error(ex.getLocalizedMessage(), ex);
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it
        if (AnnotationUtils.findAnnotation
                (ex.getClass(), ResponseStatus.class) != null) {
            throw ex;
        }
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, request, ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

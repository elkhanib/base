package com.bosch.inst.base.rest.entity;

import lombok.Data;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Data
public class ApiError {
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

    public ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status, HttpServletRequest request, Throwable ex) {
        this();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = ex.toString();
        this.path = request.getRequestURI();
    }


    public ApiError(BaseErrorDef def, HttpServletRequest request, Throwable ex) {
        this();
        this.status = def.getValue();
        this.error = def.getReasonPhrase();
        this.message = ex.toString();
        this.path = request.getRequestURI();
    }
}

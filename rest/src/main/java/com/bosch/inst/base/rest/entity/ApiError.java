package com.bosch.inst.base.rest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Data
public class ApiError {
    private Integer code;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String error;
    private String message;
    private String path;

    public ApiError() {
        timestamp = LocalDateTime.now();
    }


    public ApiError(ApiErrorDef def, HttpServletRequest request, Throwable ex) {
        this();
        this.code = def.getValue();
        this.error = def.getReasonPhrase();
        this.message = ex.toString();
        this.path = request.getRequestURI();
    }
}

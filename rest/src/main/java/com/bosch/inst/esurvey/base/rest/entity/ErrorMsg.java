package com.bosch.inst.esurvey.base.rest.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMsg {
    private Integer code;
    private String msg;
    private Object data;

    public ErrorMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

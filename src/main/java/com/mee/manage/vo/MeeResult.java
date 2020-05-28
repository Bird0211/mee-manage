package com.mee.manage.vo;


import com.mee.manage.util.StatusCode;

import lombok.Data;

@Data
public class MeeResult {

    private String description;
    private int statusCode;
    private Object data;

    public void setStatusCodeDes(StatusCode statusCode) {
        this.setStatusCode(statusCode.getCode());
        this.setDescription(statusCode.getCodeMsg());
    }
}

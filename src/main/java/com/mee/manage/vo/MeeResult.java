package com.mee.manage.vo;


import lombok.Data;

@Data
public class MeeResult {

    private String description;
    private int statusCode;
    private Object data;

}

package com.mee.manage.util;

public enum StatusCode {

    SUCCESS(0, "SUCCESS"),
    FAIL(1, "FAIL"),
    SYS_ERROR(118000, "Sys Error"),
    DB_ERROR(118001, "DB Error"),
    USER_NOT_EXIST(118002,"User Not Exist"),
    PARAM_ERROR(118003,"Param error"),
    WEIMOB_TOKEN_ERROR(118004,"Weimob token error"),
    WEIMOB_LOW_PRICE(118005,"Price is lower than Weimob price"),
    AUTH_FAIL(118006,"Auth Fail"),


    ;

    private int code;

    private String codeMsg;

    StatusCode(int code, String codeMsg) {
        this.code = code;
        this.codeMsg = codeMsg;
    }

    public int getCode() {
        return code;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

}

package com.mee.manage.util;

public enum StatusCode {

    SUCCESS(0, "SUCCESS"),
    FAIL(1, "FAIL"),
    SYS_ERROR(118000, "Sys Error"),
    DB_ERROR(118001, "DB Error"),
    USER_NOT_EXIST(118002,"User Not Exist"),
    PARAM_ERROR(118003,"Param error");

    private int code;

    private String codeMsg;

    StatusCode(int code, String codeMsg) {
        this.code = code;
        this.codeMsg = codeMsg;
    }

    public int getCode() {
        return code;
    }

    private void setCode(int code) {
        this.code = code;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    private void setCodeMsg(String codeMsg) {
        this.codeMsg = codeMsg;
    }

}

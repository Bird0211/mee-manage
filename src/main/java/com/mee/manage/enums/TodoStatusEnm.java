package com.mee.manage.enums;

public enum TodoStatusEnm {

    UNDO(0),

    DONE(1);

    Integer code;

    TodoStatusEnm(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }
    
}
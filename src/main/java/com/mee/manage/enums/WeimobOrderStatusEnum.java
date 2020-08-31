package com.mee.manage.enums;

public enum WeimobOrderStatusEnum {

    ALL(null),

    NEEDPAY(0),

    PAID(1),

    DELIVERY(2),

    COMPLETE(3),
    
    CANCEL(4);

    Integer code;

    WeimobOrderStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }

}
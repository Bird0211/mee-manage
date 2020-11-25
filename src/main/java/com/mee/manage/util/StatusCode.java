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

    OVER_TIME(11807,"Access timeout"),
    TOKEN_ERROR(11808,"Token is not exist"),
    SIGN_ERROR(11809,"sign error"),

    BIZ_NOT_EXIST(11810,"BIZ is not exist"),
    BIZ_STATUS_ERROR(11811,"BIZ ERROR"),
    BIZ_OVER_TIME(11812,"BIZ is expire"),

    FLYWAY_NOT_EXIST(11813,"FlyWay is not exist"),
    FLYWAY_LOGIN_ERROR(11814,"FlyWay Login Fail"),

    YIYUN_ORDER_ERROR(11815,"Yiyun order error"),

    PLATFORM_NOT_EXIST(11816,"PlatForm is not exist"),

    TRADEME_EXCEED_QUOTA(11817, "You have exceeded your API call quota for the current hour"),
    
    TRADEME_AUTH_ERROR(11818, "TradeMe auth error"),

    NZPOST_TOKEN_ERROR(11819, "Nzpost token error"),

    NZPOST_CONFIG_NOT_EXIST(11820, "Nzpost config is not exist"),

    PRODUCT_NOT_EXIST(11821, "Product is not exist!"),

    ORDER_STATUS_ERROR(11822, "Order status error!"),

    UGG_ORDER_ERROR(11823, "Ugg Order error!")
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

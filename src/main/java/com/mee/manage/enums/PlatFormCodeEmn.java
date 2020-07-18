package com.mee.manage.enums;

public enum PlatFormCodeEmn {

    NINETEEN("19"),

    WEIMOB("weimob"),

    FLYWAY("flyway"),

    TRADEMEE("trademe");

    String code;

    PlatFormCodeEmn(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
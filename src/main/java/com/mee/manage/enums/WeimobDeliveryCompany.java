package com.mee.manage.enums;

import org.springframework.util.StringUtils;

public enum WeimobDeliveryCompany {


    ftd("FTD","ftd","富腾达快递"),
    shunfeng("SF","shunfeng","顺丰速运"),
    flyway("CG","flyway","程光快递");

    String meeCode;

    String code;

    String name;

    WeimobDeliveryCompany(String meeCode,String code,String name){
        this.meeCode = meeCode;
        this.code = code;
        this.name = name;
    }

    public String getCode(){
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getMeecode() { return this.meeCode;}

    public static WeimobDeliveryCompany getExpCompany (String meeCode) {
        if (StringUtils.isEmpty(meeCode))
            return null;

        for (WeimobDeliveryCompany s : WeimobDeliveryCompany.values()) {
            if (meeCode.equals(s.meeCode))
                return s;
        }
        return null;
    }

    public static WeimobDeliveryCompany getExpCompanyByCode (String code) {
        if (StringUtils.isEmpty(code))
            return null;

        for (WeimobDeliveryCompany s : WeimobDeliveryCompany.values()) {
            if (code.equals(s.code))
                return s;
        }
        return null;
    }

    public static String getExpCodeByName(String name) {
        if (StringUtils.isEmpty(name))
        return null;

    for (WeimobDeliveryCompany s : WeimobDeliveryCompany.values()) {
        if (name.equals(s.getName()))
            return s.getCode();
    }

    return null;
    }

}

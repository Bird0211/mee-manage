package com.mee.manage.enums;

import lombok.Data;
import org.springframework.util.StringUtils;

public enum WeimobDeliveryCompany {


    ftd("FTD","ftd","富腾达国际货运"),
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

}

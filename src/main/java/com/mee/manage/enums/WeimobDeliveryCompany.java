package com.mee.manage.enums;

import lombok.Data;

public enum WeimobDeliveryCompany {


    ftd("ftd","富腾达国际货运");

    String code;

    String name;

    WeimobDeliveryCompany(String code,String name){
        this.code = code;
        this.name = name;
    }

    public String getCode(){
        return this.code;
    }

    public String getName() {
        return this.name;
    }


}

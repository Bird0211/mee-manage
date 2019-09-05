package com.mee.manage.enums;

import com.mee.manage.vo.InvoiceTypeVo;
import com.mee.manage.vo.Tools;
import com.mee.manage.vo.WordsVo;
import lombok.Data;

import java.util.List;

public enum InvoiceEnum {

    MITOQ("mitoq","document date",1,"order no",1,"description",
            "quantity","unit price","total",null),

    SAVAR("inclusiv","invoice date",2,"invoice number",2,"description",
            "quantity","unit price","total",null),

    EGOMALL("egomall","invoice date",2,"invoice number",2,"description",
            "quantity","unit price","total",null),

    LIVINGNATURE("living nature","invoice date",1,"invoice no:",1,"description",
            "qty","unit price","courier",null),

    PARKERGO("parker&co","due date",1,"invoices no:",1,"item",
            "unit","unit price","total","sku"),

    HEALTHCARE("healthcare","due date",1,"invoices no:",1,"item",
            "unit","unit price","total","sku"),

    DEFAUTL("null","invoice date",1,"invoices no",1,"description",
            "quantity","unit price","total","sku"),

    ;


    String keyWord;

    String dateName;

    int dateLocation;

    String noName;

    int noLocation;

    String descriptionName;

    String quantityName;

    String unitPriceName;

    String endLineName;

    String sku;

    InvoiceEnum(String keyWord,String dateName,int dateLocation,String noName,int noLocation,String descriptionName,
                        String quantityName,String unitPriceName,String endLineName,String sku){
        this.keyWord = keyWord;
        this.dateName = dateName;
        this.noName = noName;
        this.descriptionName = descriptionName;
        this.quantityName = quantityName;
        this.unitPriceName = unitPriceName;
        this.endLineName = endLineName;
        this.dateLocation = dateLocation;
        this.noLocation = noLocation;
        this.sku = sku;
    }

    public String getDateName() {
        return dateName;
    }

    public String getDescriptionName() {
        return descriptionName;
    }

    public String getEndLineName() {
        return endLineName;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public String getNoName() {
        return noName;
    }

    public String getQuantityName() {
        return quantityName;
    }

    public String getUnitPriceName() {
        return unitPriceName;
    }

    public int getDateLocation() {
        return dateLocation;
    }

    public int getNoLocation() {
        return noLocation;
    }

    public String getSku() {
        return sku;
    }
}

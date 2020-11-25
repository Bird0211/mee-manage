package com.mee.manage.vo.ugg;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class DFOrder {
    
    @JSONField(name = "WarehouseNo")
    String WarehouseNo;

    @JSONField(name="ReciverName")
    String ReciverName;

    @JSONField(name="ReciverPhone")
    String ReciverPhone;

    @JSONField(name="ReciverAddr")
    String ReciverAddr;

    @JSONField(name="ReciverCountryCode")
    Integer ReciverCountryCode;

    @JSONField(name="ReciverProvince")
    String ReciverProvince;

    @JSONField(name="ReciverCity")
    String ReciverCity;

    @JSONField(name="ReciverPostCode")
    Integer ReciverPostCode;

    @JSONField(name="SenderName")
    String SenderName;

    @JSONField(name="SenderPhone")
    String SenderPhone;

    @JSONField(name="Barcode")
    String Barcode;

    @JSONField(name="OrderQty")
    Integer OrderQty;

    @JSONField(name="TrafficType")
    Integer TrafficType;

    @JSONField(name="Issubmit")
    Integer Issubmit;

    @JSONField(name="Remark")
    String Remark;

    @JSONField(name="ClientOrderNo")
    String ClientOrderNo;

}

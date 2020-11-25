package com.mee.manage.vo.ugg;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class QueryOrder {
    
    @JSONField(name = "BeginCreateDate")
    String BeginCreateDate;

    @JSONField(name = "EndCreateDate")
    String EndCreateDate;

    @JSONField(name = "StatusList")
    List<Integer> StatusList;

    @JSONField(name = "CurrentPageIndex")
    Integer CurrentPageIndex;

    @JSONField(name = "PageSize")
    Integer PageSize;

    @JSONField(name = "WarehouseCode")
    String WarehouseCode;

    @JSONField(name = "ClientOrderNo")
    String ClientOrderNo;

    @JSONField(name = "OrderSheetId")
    String OrderSheetId;

    @JSONField(name = "ProductNo")
    String ProductNo;

    @JSONField(name = "ReciverName")
    String ReciverName;

    @JSONField(name = "ReciverPhone")
    String ReciverPhone;

    @JSONField(name = "SortField")
    String SortField;

    @JSONField(name = "ZoneId")
    String ZoneId;

    @JSONField(name = "WarehouseId")
    String WarehouseId;
}

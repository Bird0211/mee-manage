package com.mee.manage.vo.ugg;

import java.util.List;

import lombok.Data;

@Data
public class QueryOrderRsp {
    
    Integer PageCount;

    Integer RecordTotal;

    Integer PageSize;

    Integer CurrentPageIndex;

    String SortField;

    List<UggOrderData> Data;
}

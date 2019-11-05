package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class WeimobSingleRequest {
    Long orderNo;
    String deliveryNo;
    String deliveryCompanyCode;
    String deliveryCompanyName;
    Boolean isNeedLogistics;
    Boolean isSplitPackage;
    String deliveryRemark;
    Long deliveryOrderId;

    List<WeimobSingleSku> deliveryOrderItemList;


}

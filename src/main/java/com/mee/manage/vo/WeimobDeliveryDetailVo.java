package com.mee.manage.vo;

import lombok.Data;

import java.util.Date;

@Data
public class WeimobDeliveryDetailVo {

    Integer deliveryType;

    String deliveryTypeName;

    String expectDeliveryTime;

    boolean isSplitPackage;

    LogisticsDeliveryDetail logisticsDeliveryDetail;


}

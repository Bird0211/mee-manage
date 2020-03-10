package com.mee.manage.vo.weimob;

import com.mee.manage.vo.LogisticsDeliveryDetail;
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

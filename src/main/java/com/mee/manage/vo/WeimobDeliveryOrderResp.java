package com.mee.manage.vo;

import lombok.Data;

@Data
public class WeimobDeliveryOrderResp {

    WeimobOrderCode code;

    WeimobDeliveryOrderData data;

    DeliveryOrderVo deliveryOrder;

}

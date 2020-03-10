package com.mee.manage.vo.weimob;

import com.mee.manage.vo.DeliveryOrderVo;
import lombok.Data;

@Data
public class WeimobDeliveryOrderResp {

    WeimobOrderCode code;

    WeimobDeliveryOrderData data;

    DeliveryOrderVo deliveryOrder;

}

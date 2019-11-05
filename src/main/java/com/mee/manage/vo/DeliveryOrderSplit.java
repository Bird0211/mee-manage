package com.mee.manage.vo;


import lombok.Data;

import java.util.List;

@Data
public class DeliveryOrderSplit {

    List<DeliveryOrderVo> deleverBatchOrders;

    List<DeliveryOrderVo> deleverSingleOrders;


}

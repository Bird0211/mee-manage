package com.mee.manage.vo.weimob;

import lombok.Data;

@Data
public class WeimobBatchDelivery {

    Long orderNo;

    String deliveryNo;

    String deliveryCompanyCode;

    String deliveryCompanyName;

    boolean isNeedLogistics;

}

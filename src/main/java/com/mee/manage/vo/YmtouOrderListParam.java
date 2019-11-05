package com.mee.manage.vo;

import lombok.Data;

import java.util.Date;

@Data
public class YmtouOrderListParam {

    /*
    订单状态(逗号分隔多个状态,传空为全状态)
    未付款:ORDER_ESTABLISH(1),
    已付款待接单:ACCOUNT_PAID(2),
    已发货:SHIPPED(3),
    确认收货:RECEIVED(4),
    买家取消订单:USER_ACCEPT_CANCEL(12),
    卖家取消订单:SELLER_ACCEPT_CANCEL(13),
    系统自动取消:SYSTEM_CANCEL(18),
    已接单:SELLER_ACCEPT(17)
    */
    String orderStatus;

    Date startDate;

    Date endDate;

    Integer pageNo;

    Integer pageRows;

}

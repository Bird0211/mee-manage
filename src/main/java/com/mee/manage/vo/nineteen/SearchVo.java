package com.mee.manage.vo.nineteen;

import lombok.Data;

/**
 * SearchVo
 */
@Data
public class SearchVo {

    String createStartTime;

    String createEndTime;

    String payStartTime;

    String payEndTime;

    /**
     * 订单状态
        0:待支付
        1:待发货
        2:已收货
        3:已完成
        5:申请退款
        6:退款成功
     */
    Integer state; 
}
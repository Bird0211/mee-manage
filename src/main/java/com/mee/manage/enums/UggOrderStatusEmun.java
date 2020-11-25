package com.mee.manage.enums;

public enum UggOrderStatusEmun {

    Pretreatment(1),             //预处理, 待结算
    PrePay(2),                        //生成结算单, 待支付
    PreDelivery(3),                        //用户已支付, 订单待发货
    Delivery(4),                        //订单已发货
    Fail(5);                        //订单发货失败


    Integer code;

    UggOrderStatusEmun(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }
    
}

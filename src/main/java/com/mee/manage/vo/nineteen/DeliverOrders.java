package com.mee.manage.vo.nineteen;

import java.util.List;

import lombok.Data;

@Data
public class DeliverOrders {
    
    //订单Id
    String orderId; 

    String tradeNo;

    //快递单号
    String courierNumber;

    //收件人姓名
    String name;

    //收件人地址
    String address;

    //收件人电话
    String phone;
    
    List<NineTeenOrderDetail> orderDetails; 

}

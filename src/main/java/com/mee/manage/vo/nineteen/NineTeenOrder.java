package com.mee.manage.vo.nineteen;

import java.util.List;

import lombok.Data;

/**
 * NineTeenOrder
 */
@Data
public class NineTeenOrder {

    //订单状态
    String state;

    //收件人
    String collection_name;

    String collection_address;

    String collection_phone;

    //身份证姓名
    String identity_name;

    //发件人
    String sender_name;

    String pay_time;

    //快递单
    String courier_number;

    //身份证
    String identity_number;

    String create_time;

    String phone;

    String sender_address;

    Long order_id;

    String trade_no;

    String remarks;

    List<NineTeenOrderDetail> order_detail;

}
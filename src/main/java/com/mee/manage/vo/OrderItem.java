package com.mee.manage.vo;

import java.util.List;

import lombok.Data;

/**
 * OrderItem
 */
@Data
public class OrderItem {
    List<ProductVo> products;
    int num;
    String address;
    String phone;
    String name;    //收件人姓名
    String orderNo;
    String idCardNo;    //身份证号
    String sender;  //发件人姓名
    String remark;  //备注
    String senderPhone;
}
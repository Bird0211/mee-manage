package com.mee.manage.vo;

import java.util.List;

import lombok.Data;

@Data
public class FlywayorderDetail {
    String postType;

    String receiverName;

    String receiverPhone;

    String receiverAddr;

    String senderName;

    String senderPhone;

    String senderAddr;

    String remark;
    
    List<String> productsName;

    List<String> productsQuantity;


}
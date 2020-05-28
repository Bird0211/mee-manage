package com.mee.manage.vo;

import lombok.Data;

@Data
public class FlywayOrderResponse {
    String postType;

    String receiverName;

    String receiverPhone;

    String receiverAddr;

    Long id;

    Long orderNumber;

    Integer clientID;

    String senderName;

    String senderPhone;

    String senderAddr;

    String weight;

    String remark;

    String productName;

    String productQuantity;

    String batchNumber;

    boolean removed;

    boolean picked;
}
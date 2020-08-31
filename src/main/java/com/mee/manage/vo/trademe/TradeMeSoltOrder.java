package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class TradeMeSoltOrder {
    
    String orderId;

    Integer purchaseId;

    String reference;

    String soldDate;

    SoltItemBuyer buyer;

    SoltItemDeliveryAddress deliveryAddress;

    SoltItemPaymentDetails PaymentDetail;

    SoltItem items;

}
package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemFullInfoVo {

    String rightsStatusName;
    Integer rightsStatus;
    Long rightsOrderId;
    Integer commentStatus;
    BigDecimal shouldPaymentAmount;
    Long id	;
    BigDecimal skuAmount;
    BigDecimal totalAmount;
    BigDecimal paymentAmount;
    Long goodsId;
    Long skuId;
    String imageUrl;
    String skuName;
    Integer skuNum;
    Long goodsCategoryId;
    BigDecimal price;
    String goodsCode;
    String skuCode;
    Integer hadDeliveryItemNum;
    BigDecimal originalPrice;
    Long totalPoint;
    Long point;
    Integer goodsType;
    String goodsTitle;

}

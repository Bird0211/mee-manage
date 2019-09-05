package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WeimobItem {

     Integer commentStatus;
     Long goodsCategoryId;
     String goodsCode;
     Long goodsId;
     String goodsTitle;
     Integer goodsType;
     Integer hadDeliveryItemNum;
     Long id;
     String imageUrl;
     BigDecimal originalPrice;
     BigDecimal paymentAmount;
     String point;
     BigDecimal price;
     Long rightsOrderId;
     Long rightsStatus;
     String rightsStatusName;
     BigDecimal shouldPaymentAmount;
     BigDecimal skuAmount;
     String skuCode;
     Long skuId;
     String skuName;
     Integer skuNum;
     WeimobBizInfo bizInfo;


}

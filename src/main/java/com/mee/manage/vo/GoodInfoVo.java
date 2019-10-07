package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodInfoVo {

    Long goodsId;
    String title;
    String defaultImageUrl;
    String sku;
    Long skuId;
    BigDecimal costPrice;
    BigDecimal salesPrice;
    BigDecimal yiyunCostPrice;
    BigDecimal yiyunSalesPrice;
    BigDecimal originalPrice;
    String weight;
}

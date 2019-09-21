package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodInfoVo {

    Long goodsId;
    String title;
    String defaultImageUrl;
    String sku;
    BigDecimal costPrice;
    BigDecimal salesPrice;

    BigDecimal yiyunCostPrice;
    BigDecimal yiyunSalesPrice;
    String weight;
}

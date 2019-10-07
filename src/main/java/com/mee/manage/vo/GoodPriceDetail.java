package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodPriceDetail {

    private Long goodId;

    private Long skuId;

    private String sku;

    private BigDecimal originalPrice;

    private BigDecimal updateCostPrice;

    private BigDecimal updateSalesPrice;
}

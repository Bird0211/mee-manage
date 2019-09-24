package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuList {

    Long skuId;

    BigDecimal salePrice;

    BigDecimal originalPrice;

    BigDecimal costPrice;

    Integer editStockNum;
}

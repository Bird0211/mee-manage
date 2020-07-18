package com.mee.manage.vo.nineteen;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class NineTeenSku {
    
    String sku;

    String skuId;

    Integer stock;

    String name;

    BigDecimal price;

    BigDecimal firstLevel;

    BigDecimal secondLevel;

    BigDecimal thirdLevel;

    BigDecimal fourthLevel;

    BigDecimal fifthLevel;

    BigDecimal sixthLevel;

    Double weight;
}
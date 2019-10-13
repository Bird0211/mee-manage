package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComparePricesVo {

    String sku;

    String name;

    BigDecimal costPrice;

    BigDecimal newPrice;

}

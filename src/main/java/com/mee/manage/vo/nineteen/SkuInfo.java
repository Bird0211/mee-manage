package com.mee.manage.vo.nineteen;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SkuInfo {
   
    String spec_name;

    BigDecimal sku_price;

    Double weight;

    String sku_code;

    String sku_id;

    Integer sku_stock;

    BigDecimal first_level;

    BigDecimal second_level;

    BigDecimal third_level;

    BigDecimal fourth_level;

    BigDecimal fifth_level;

    BigDecimal sixth_level;


}
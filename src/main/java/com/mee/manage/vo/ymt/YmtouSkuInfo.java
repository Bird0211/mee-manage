package com.mee.manage.vo.ymt;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class YmtouSkuInfo {

    String sku_id;
    String outer_id;
    Boolean used;
    BigDecimal price;

    BigDecimal vip_price;
    BigDecimal new_price;
    Integer stock_num;
    Double weight;
    Integer weight_unit;
    String extra_info;


}

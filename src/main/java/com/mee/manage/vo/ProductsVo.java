package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductsVo {

    String content;

    Double num;

    BigDecimal price;

    String sku;
}

package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceProduct {

    String sku;

    BigDecimal price;

    Integer num;


}

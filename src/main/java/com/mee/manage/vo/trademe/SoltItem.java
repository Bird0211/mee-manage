package com.mee.manage.vo.trademe;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SoltItem {
    
    String name;

    String sku;

    String photo;

    Integer quantity;

    BigDecimal price;
    
}
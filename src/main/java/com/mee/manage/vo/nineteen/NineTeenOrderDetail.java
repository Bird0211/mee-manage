package com.mee.manage.vo.nineteen;

import java.math.BigDecimal;

import lombok.Data;

/**
 * NineTeenOrderDetail
 */
@Data
public class NineTeenOrderDetail {

    BigDecimal price;

    Integer num;

    String code;

    String name_ch;

    String name_eh;

    String name;

    String sku;
    
}
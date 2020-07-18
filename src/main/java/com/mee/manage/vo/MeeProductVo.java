package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MeeProductVo {
    String id;

    String code;

    String name;

    String brand;

    String chName;

    String weight;

    BigDecimal costPrice;       //成本价

    BigDecimal retailPrice;     //销售价
}

package com.mee.manage.vo;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProVo {

    private Long sku;

    private String name;

    private String overseaName;

    private Integer categoryId;

    private BigDecimal costPrice;

    private BigDecimal retailPrice;

    private BigDecimal overseaCostPrice;

    private BigDecimal overseaRetailPrice;

    private String brand;

    private Integer weight;

}

package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpDetailVo {
    private String orderId;

    private String sku;

    private int num;

    private String sender;

    private String name;

    //单价
    private BigDecimal unitprice;

    private int weight;

    //总价
    private BigDecimal totalprice;

}

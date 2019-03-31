package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SettleFeeVo {

    private String orderId;

    private String name;

    private Long expId;

    private String phone;

    private String address;

    private List<ProductVo> products;

    private BigDecimal totalFee;

    List<FeeDetailVo> feeDetail;


}

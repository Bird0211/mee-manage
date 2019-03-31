package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FeeDetailVo {


    private BigDecimal fee;

    private int feeType;

    private String feeTypeName;

    private String remark;

    private Object details;

}

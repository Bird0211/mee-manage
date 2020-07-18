package com.mee.manage.vo.Yiyun;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class YiyunTodayData {
    
    //总金额
    BigDecimal totalPrice;

    //总数量
    Integer totalNum;

    //已发货
    Long deliveredNum;

    //待发货
    Long undeliveredNum;

}
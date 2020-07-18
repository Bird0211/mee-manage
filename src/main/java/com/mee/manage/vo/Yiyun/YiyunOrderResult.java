package com.mee.manage.vo.Yiyun;

import java.util.List;

import lombok.Data;

@Data
public class YiyunOrderResult {
    String result;

    String error;

    List<YiyunOrderSales> sales;
}
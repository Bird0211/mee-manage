package com.mee.manage.service;

import java.util.List;

import com.mee.manage.vo.Yiyun.YiyunOrderSales;

public interface IHandleOrder<T> {

    T handle(List<YiyunOrderSales> salesOrders);
    
}
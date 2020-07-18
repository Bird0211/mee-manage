package com.mee.manage.service;

import java.util.List;

import com.mee.manage.exception.MeeException;
import com.mee.manage.vo.DataTotal;
import com.mee.manage.vo.OrderStatisticsData;
import com.mee.manage.vo.YiyunOrderDatePeriod;
import com.mee.manage.vo.Yiyun.YiyunErrorVo;
import com.mee.manage.vo.Yiyun.YiyunNoShipVo;
import com.mee.manage.vo.Yiyun.YiyunOrderSales;
import com.mee.manage.vo.Yiyun.YiyunOrderVo;
import com.mee.manage.vo.Yiyun.YiyunTodayData;
import com.mee.manage.vo.Yiyun.YiyunTopProduct;

public interface IOrderService {

    List<YiyunOrderSales> getYiyunOrder(Integer bizId, YiyunOrderVo orderVo);

    YiyunTodayData getTodayData(Integer bizId);

    DataTotal getTotalData(Integer bizId);

    YiyunNoShipVo getNoShipData(Integer bizId);

    YiyunErrorVo getErrorData(Integer bizId);

    List<OrderStatisticsData> getStatistionDatas(Integer bizId, YiyunOrderDatePeriod orderVo);

    <T> List<T> handleYiyunOrder(Integer bizId, YiyunOrderVo orderVo, IHandleOrder<T> handleOrder) throws MeeException;

    List<YiyunTopProduct> getTopProducts(Integer bizId, YiyunOrderVo orderVo, Integer limit);

    List<YiyunTopProduct> getTopProductsByDays(Integer bizId, Integer oVo, Integer limit);



    
}
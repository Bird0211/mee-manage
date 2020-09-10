package com.mee.manage.service;

import java.util.List;
import java.util.Set;

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

    List<YiyunOrderSales> getYiyunOrder(Long bizId, YiyunOrderVo orderVo);

    List<YiyunOrderSales> getYiyunOrderByExtId(Long bizId, Set<String> extId);

    YiyunTodayData getTodayData(Long bizId);

    DataTotal getTotalData(Long bizId);

    YiyunNoShipVo getNoShipData(Long bizId);

    YiyunErrorVo getErrorData(Long bizId);

    List<OrderStatisticsData> getStatistionDatas(Long bizId, YiyunOrderDatePeriod orderVo);

    <T> List<T> handleYiyunOrder(Long bizId, YiyunOrderVo orderVo, IHandleOrder<T> handleOrder) throws MeeException;

    <T> List<T> handleYiyunOrder(Long bizId, Set<String> extIds, IHandleOrder<T> handleOrder) throws MeeException;

    List<YiyunTopProduct> getTopProducts(Long bizId, YiyunOrderVo orderVo, Integer limit);

    List<YiyunTopProduct> getTopProductsByDays(Long bizId, Integer oVo, Integer limit);



    
}
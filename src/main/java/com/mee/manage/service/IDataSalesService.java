package com.mee.manage.service;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.DataSales;
import com.mee.manage.vo.DataTotal;
import com.mee.manage.vo.OrderStatisticsData;

public interface IDataSalesService extends IService<DataSales> {

    public void initData();

    void initData(Long bizId);
    
    DataSales getLastData(Long bizId);

    DataTotal getTotalData(Long bizId);

    List<DataSales> getDatas(Long bizId, Date from, Date to);

    List<OrderStatisticsData> getDatasDay(Long bizId, Date from, Date to);
    
}
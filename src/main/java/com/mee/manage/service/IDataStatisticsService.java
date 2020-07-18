package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.DataStatistics;

public interface IDataStatisticsService extends IService<DataStatistics> {

    DataStatistics getErrorOrder(Integer bizId);

    boolean saveErrorOrder(DataStatistics data);

    boolean saveStaticOrder(Integer bizId);

    boolean saveTopProduct(Integer bizId);
    
}
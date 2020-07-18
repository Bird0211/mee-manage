package com.mee.manage.service.impl;
import java.util.List;

import com.mee.manage.po.Biz;
import com.mee.manage.service.IBizService;
import com.mee.manage.service.IDataSalesService;
import com.mee.manage.service.IDataStatisticsService;
import com.mee.manage.service.IJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class JobServiceImpl implements IJob {

    @Autowired
    IDataSalesService dataSales;

    @Autowired
    IDataStatisticsService dataStatistics;

    @Autowired
    IBizService bizService;

    @Override
    @Scheduled(cron="0 0 5 * * ?")
    public void loadYiyunData() {
        dataSales.initData();
    }

    @Override
    @Scheduled(cron="0 0/10 * * * ?")
	public void loadErrorData() {
        List<Biz> allBiz = bizService.getAllBiz();
        if(allBiz != null) {
            for(Biz biz: allBiz) {
                dataStatistics.saveStaticOrder(biz.getId());
            }
        }
	}

    @Override
    @Scheduled(cron="0 0 6 * * ?")
	public void loadTopOrderData() {
        
        List<Biz> allBiz = bizService.getAllBiz();
        if(allBiz != null) {
            for(Biz biz: allBiz) {
                dataStatistics.saveTopProduct(biz.getId());
            }
        }
		
	}

    
    
}
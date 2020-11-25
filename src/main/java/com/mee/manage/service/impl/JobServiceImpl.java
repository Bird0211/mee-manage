package com.mee.manage.service.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.mee.manage.po.Biz;
import com.mee.manage.service.IBizService;
import com.mee.manage.service.IDataSalesService;
import com.mee.manage.service.IDataStatisticsService;
import com.mee.manage.service.IJob;
import com.mee.manage.service.IUggService;
import com.mee.manage.service.IWeimobService;
import com.mee.manage.util.DateUtil;
import com.mee.manage.vo.nineteen.DeliverOrders;
import com.mee.manage.vo.ugg.QueryOrder;
import com.mee.manage.vo.ugg.QueryOrderRsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class JobServiceImpl implements IJob {

    protected static final Logger logger = LoggerFactory.getLogger(IJob.class);

    @Autowired
    IDataSalesService dataSales;

    @Autowired
    IDataStatisticsService dataStatistics;

    @Autowired
    IBizService bizService;

    @Autowired
    IUggService uggService;

    @Autowired
    IWeimobService weimobService;

    @Override
    // @Scheduled(cron="0 0 5 * * ?")
    public void loadYiyunData() {
        dataSales.initData();
    }

    @Override
    // @Scheduled(cron="0 0/10 * * * ?")
	public void loadErrorData() {
        List<Biz> allBiz = bizService.getAllBiz();
        if(allBiz != null) {
            for(Biz biz: allBiz) {
                dataStatistics.saveStaticOrder(biz.getId());
            }
        }
	}

    @Override
    // @Scheduled(cron="0 0 6 * * ?")
	public void loadTopOrderData() {
        
        List<Biz> allBiz = bizService.getAllBiz();
        if(allBiz != null) {
            for(Biz biz: allBiz) {
                dataStatistics.saveTopProduct(biz.getId());
            }
        }
		
    }

    @Override
    @Scheduled(cron="0 0 19 * * ?")
    public void loadUggDelivery() {
        int pageIndex = 1;
        String beginDate = DateUtil.dateToStringFormat(DateUtil.getPrefixDate(7), DateUtil.formatPattern) ;
        String endDate = DateUtil.getCurrentDate();

        QueryOrder params = new QueryOrder();
        params.setBeginCreateDate(beginDate);
        params.setEndCreateDate(endDate);
        params.setCurrentPageIndex(pageIndex);
        params.setPageSize(25);
        params.setStatusList(Lists.newArrayList(4,5));
        params.setZoneId("500001");
        
        logger.info("--------- Load UGG Delivery ----------");
        int totalPages = 0;
        do {
            QueryOrderRsp rsp = uggService.queryUggOrders(params, 20L);
            if(rsp != null && rsp.getData() != null && !rsp.getData().isEmpty()) {
                try {
                    logger.info("Query Ugg Order  = {}", rsp.getRecordTotal());
                    uggService.deliveryOrder(rsp.getData());
                } catch(Exception ex ) {
                    ex.printStackTrace();
                    logger.error("Delivery Error", ex);
                }
                totalPages = rsp.getPageCount();
            }
            pageIndex ++;
        } while (pageIndex <= totalPages);
        logger.info("--------- Load UGG Delivery END----------");
    }

    @Override
    @Scheduled(cron="0 0 0/2 * * ?")
    public void refreshWeimobToken() {
        List<Biz> allBiz = bizService.getAllBiz();
        if(allBiz != null) {
            for(Biz biz: allBiz) {
                weimobService.checkToken(biz.getId());
            }
        }
    }
}
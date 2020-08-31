package com.mee.manage.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.word.checker.util.StringUtil;
import com.google.common.collect.Lists;
import com.mee.manage.config.Config;
import com.mee.manage.mapper.IDataSalesMapper;
import com.mee.manage.po.DataSales;
import com.mee.manage.service.IConfigurationService;
import com.mee.manage.service.IDataSalesService;
import com.mee.manage.service.IOrderService;
import com.mee.manage.util.DateUtil;
import com.mee.manage.util.StrUtil;
import com.mee.manage.vo.DataTotal;
import com.mee.manage.vo.OrderStatisticsData;
import com.mee.manage.vo.Yiyun.YiyunOrderSales;
import com.mee.manage.vo.Yiyun.YiyunOrderVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSalesServiceImpl extends ServiceImpl<IDataSalesMapper, DataSales> implements IDataSalesService {

    protected static final Logger logger = LoggerFactory.getLogger(IDataSalesService.class);

    @Autowired
    IOrderService orderService;

    @Autowired
    Config config;

    @Autowired
    IConfigurationService configService;

    @Override
    public void initData() {
        String bizIds = configService.getValue(Config.DATA_BIZID);
        if(bizIds == null) {
            return ;
        }

        String[] bizId = bizIds.split(";");
        for(String id: bizId) {
            if(StringUtil.isEmpty(id))
                continue;
            initData(Long.parseLong(id));
        }
    }

    @Override
    public void initData(Long bizId) {
        String startDate = null;
        DataSales dataSales = getLastData(bizId);
        if(dataSales == null) {
            startDate = config.getStartDate();
        } else {
            Date yesterday = DateUtil.getPrefixDate(1);
            String sData = DateUtil.dateToStringFormat(dataSales.getSalesDate(), DateUtil.formatPattern);
            String yData = DateUtil.dateToStringFormat(yesterday, DateUtil.formatPattern);
            if(sData.equals(yData)) {
                logger.info("BizId: {} ; Data {} has been load", bizId, sData);
                return ;
            }
            startDate = DateUtil.dateToStringFormat(DateUtil.getSuffixDate(dataSales.getSalesDate(),1), DateUtil.formatPattern);
            logger.info("Load Date: {}", dataSales.getSalesDate());
            logger.info("startDate: {}", startDate);
        }

        this.loadYiyunOrders(bizId, startDate);
    }

    @Override
    public DataSales getLastData(Long bizId) {
        QueryWrapper<DataSales> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.orderByDesc("sales_date");
        queryWrapper.last("LIMIT 1");

        return getOne(queryWrapper);
    }

    private void loadYiyunOrders(Long bizId, String date) {
        logger.info("Init Yiyun Data: {}", date);
        String endDate = DateUtil.dateToStringFormat(new Date(), DateUtil.formatPattern);
        Date searchDate = DateUtil.stringToDate(date, DateUtil.formatPattern);
        logger.info("Search Date {}", searchDate);
        YiyunOrderVo orderVo = new YiyunOrderVo();
        if(searchDate.after(new Date()) || date.equals(endDate)) {
            return ;
        }
        orderVo.setFrom(date);
        orderVo.setTo(date);

        List<YiyunOrderSales> yiyunOrders = orderService.getYiyunOrder( bizId, orderVo);
        DataTotal dateTotal = changeToDataSale(yiyunOrders);
        DataSales dataSales = new DataSales();

        dataSales.setBizId(bizId);
        dataSales.setSalesDate(searchDate);
        dataSales.setTotalNumber(dateTotal.getTotalNumber());
        dataSales.setTotalPrice(dateTotal.getTotalPrice());

        boolean flag = false;
        try {
            flag = save(dataSales);
        } catch (Exception e) {
            logger.error("save order Error{}",dataSales, e);
        }
        if(!flag) {
            logger.info("loadYiyunOrders error date : {}" , date);
        }
        searchDate = DateUtil.getSuffixDate(searchDate, 1);
        loadYiyunOrders(bizId, DateUtil.dateToStringFormat(searchDate, DateUtil.formatPattern));
    }

    private DataTotal changeToDataSale(List<YiyunOrderSales> yiyunOrders) {
        DataTotal totalData = new DataTotal();
        totalData.setTotalNumber(0L);
        totalData.setTotalPrice(BigDecimal.ZERO);

        if(yiyunOrders == null || yiyunOrders.size() <= 0)
            return totalData;

        Long number = Long.valueOf(yiyunOrders.size());
        BigDecimal price = yiyunOrders.stream().map(item -> StrUtil.readStrAsBigDecimalAndCheckFormat(item.getFullTotal()))
            .reduce(BigDecimal.ZERO,BigDecimal::add); // 使用reduce聚合函数,实现累加器
        totalData.setTotalNumber(number);
        totalData.setTotalPrice(price);
        return totalData;
    }

    @Override
    public DataTotal getTotalData(Long bizId) {
        DataTotal totalData = new DataTotal();
        totalData.setTotalNumber(0L);
        totalData.setTotalPrice(BigDecimal.ZERO);

        QueryWrapper<DataSales> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.select("IFNULL(sum(total_number),0) as total, IFNULL(sum(total_price),0) as price");

        Map<String, Object> map = getMap(queryWrapper);
        if(map != null) {
            String total = map.get("total").toString();
            totalData.setTotalNumber(Long.parseLong(total));

            String price = map.get("price").toString();
            totalData.setTotalPrice(new BigDecimal(price));
        }
        
        return totalData;
    }

    @Override
    public List<DataSales> getDatas(Long bizId, Date from, Date to) {
        QueryWrapper<DataSales> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.between("sales_date", from, to);
        return list(queryWrapper);
    }

    @Override
    public List<OrderStatisticsData> getDatasDay(Long bizId, Date from, Date to) {
        logger.info("from: {}" , from);
        logger.info("to: {}" , to);
        QueryWrapper<DataSales> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.between("sales_date", from, to);
        queryWrapper.select("IFNULL(sum(total_number),0) as total, IFNULL(sum(total_price),0) as price, sales_date as time ");
        queryWrapper.groupBy("sales_date");

        List<Map<String, Object>> maps = listMaps(queryWrapper);
        List<OrderStatisticsData> totals = null;
        if(maps != null) {
            totals = Lists.newArrayList();
            for(Map<String, Object> map: maps) {
                DataTotal totalData = new DataTotal();
                totalData.setTotalNumber(0L);
                totalData.setTotalPrice(BigDecimal.ZERO);
                
                String total = map.get("total").toString();
                totalData.setTotalNumber(Long.parseLong(total));
    
                String price = map.get("price").toString();
                totalData.setTotalPrice(new BigDecimal(price));

                String time = map.get("time").toString();

                OrderStatisticsData data = new OrderStatisticsData();
                data.setDataTotal(totalData);
                data.setTime(time);
                totals.add(data);
            }
        }
        return totals;
    }
    
    
}
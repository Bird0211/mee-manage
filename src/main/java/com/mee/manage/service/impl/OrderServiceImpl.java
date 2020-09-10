package com.mee.manage.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mee.manage.config.Config;
import com.mee.manage.config.MeeConfig;
import com.mee.manage.exception.MeeException;
import com.mee.manage.po.Biz;
import com.mee.manage.service.IBizService;
import com.mee.manage.service.IDataSalesService;
import com.mee.manage.service.IHandleOrder;
import com.mee.manage.service.IOrderService;
import com.mee.manage.util.DateUtil;
import com.mee.manage.util.GuavaExecutors;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.util.MD5Util;
import com.mee.manage.util.StatusCode;
import com.mee.manage.util.StrUtil;
import com.mee.manage.vo.DataTotal;
import com.mee.manage.vo.OrderStatisticsData;
import com.mee.manage.vo.YiyunOrderDatePeriod;
import com.mee.manage.vo.Yiyun.YiyunErrorVo;
import com.mee.manage.vo.Yiyun.YiyunNoShipVo;
import com.mee.manage.vo.Yiyun.YiyunOrderDetail;
import com.mee.manage.vo.Yiyun.YiyunOrderResult;
import com.mee.manage.vo.Yiyun.YiyunOrderSales;
import com.mee.manage.vo.Yiyun.YiyunOrderVo;
import com.mee.manage.vo.Yiyun.YiyunTodayData;
import com.mee.manage.vo.Yiyun.YiyunTopProduct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements IOrderService {

    private static final Logger logger = LoggerFactory.getLogger(IOrderService.class);

    @Autowired
    Config config;

    @Autowired
    IBizService bizService;

    @Autowired
    IDataSalesService dataService;

    @Override
    public List<YiyunOrderSales> getYiyunOrder(Long bizId, YiyunOrderVo orderVo) throws MeeException {

        List<YiyunOrderSales> sales = null;

        Biz biz = bizService.getBiz(bizId);
        if (biz == null) {
            throw new MeeException(StatusCode.BIZ_NOT_EXIST);
        }

        if (biz.getExpireDate() != null && biz.getExpireDate().before(new Date())) {
            throw new MeeException(StatusCode.BIZ_OVER_TIME);
        }

        if (biz.getStatus() == 1) {
            throw new MeeException(StatusCode.BIZ_STATUS_ERROR);
        }

        String token = biz.getToken();

        String nonce = MeeConfig.getNonce();

        Long time = DateUtil.getCurrentTime();

        String sign = getSigh(bizId, time.toString(), nonce, token);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("bizid", bizId);
        paramMap.put("from", orderVo.getFrom());
        paramMap.put("to", orderVo.getTo());
        paramMap.put("time", time);
        paramMap.put("nonce", nonce);
        paramMap.put("sign", sign);
        if(StringUtils.isNotEmpty(orderVo.getExternalSaleId())) 
            paramMap.put("externalSaleID", orderVo.getExternalSaleId());

        logger.info("Param: {}", paramMap);
        String result = JoddHttpUtils.sendPost(config.getBizSalesUrl(), paramMap);

        List<YiyunOrderResult> orders = JSON.parseArray(result, YiyunOrderResult.class);
        if (orders == null || orders.size() <= 0) {
            throw new MeeException(StatusCode.SYS_ERROR);
        }

        YiyunOrderResult oVo = orders.get(0);
        if (oVo.getResult() != null && oVo.getResult().equals("SUCCESS")) {
            sales = oVo.getSales();

        } else {
            throw new MeeException(StatusCode.YIYUN_ORDER_ERROR);
        }

        return sales;
    }

    private String getSigh(Long bizId, String time, String nonce, String token) {
        String md5Str = bizId + time + nonce + token;

        String md5ign = MD5Util.MD5Encode(md5Str, MD5Util.UTF8, false);
        String sign = null;
        try {
            sign = URLEncoder.encode(md5ign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return sign;
    }

    @Override
    public YiyunTodayData getTodayData(Long bizId) {
        YiyunOrderVo yOrderVo = new YiyunOrderVo();
        yOrderVo.setFrom(DateUtil.getCurrentDate());
        yOrderVo.setTo(DateUtil.getCurrentDate());

        YiyunTodayData todayData = new YiyunTodayData();

        List<YiyunOrderSales> yOrderSales = getYiyunOrder(bizId, yOrderVo);
        if (yOrderSales == null || yOrderSales.size() <= 0) {
            todayData.setTotalNum(0);
            todayData.setTotalPrice(BigDecimal.ZERO);
            todayData.setDeliveredNum(0L);
            todayData.setUndeliveredNum(0L);
        } else {
            Integer totalNum = yOrderSales.size();
            Long deliveredNum = yOrderSales.stream().filter(item -> item.getStatus().equals("已出库完成")).count();
            Long unDeliveredNum = yOrderSales.stream().filter(item -> item.getStatus().equals("已打单")).count();

            BigDecimal totalPrice = yOrderSales.stream()
                    .map(item -> StrUtil.readStrAsBigDecimalAndCheckFormat(item.getFullTotal()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // 使用reduce聚合函数,实现累加器

            todayData.setTotalNum(totalNum);
            todayData.setTotalPrice(totalPrice);
            todayData.setDeliveredNum(deliveredNum);
            todayData.setUndeliveredNum(unDeliveredNum);
        }
        return todayData;
    }

    @Override
    public DataTotal getTotalData(Long bizId) {
        DataTotal dataTotal = dataService.getTotalData(bizId);
        YiyunTodayData todayData = getTodayData(bizId);

        if (todayData != null) {
            dataTotal.setTotalNumber(dataTotal.getTotalNumber() + todayData.getTotalNum());
            dataTotal.setTotalPrice(dataTotal.getTotalPrice().add(todayData.getTotalPrice()));
        }
        return dataTotal;
    }

    @Override
    public YiyunNoShipVo getNoShipData(Long bizId) {
        YiyunOrderVo oVo = getYiyunOrderVo(7, 0);
        long noShipNum = getNoshipNumber(bizId, oVo);

        YiyunNoShipVo noShipVo = new YiyunNoShipVo();
        noShipVo.setNoShipOrder(noShipNum);

        return noShipVo;
    }

    @Override
    public YiyunErrorVo getErrorData(Long bizId) {
        YiyunOrderVo oVo = getYiyunOrderVo(30, 7);
        long errorNum = getNoshipNumber(bizId, oVo);

        YiyunErrorVo errorVo = new YiyunErrorVo();
        errorVo.setErrorOrder(errorNum);

        return errorVo;
    }

    private Long getNoshipNumber(Long bizId, YiyunOrderVo oVo) {

        List<Long> result = handleYiyunOrder(bizId, oVo, new IHandleOrder<Long>() {

            @Override
            public Long handle(List<YiyunOrderSales> salesOrders) {
                Long number = 0L;
                if (salesOrders != null && salesOrders.size() > 0) {
                    number = salesOrders.stream().filter(item -> item.getStatus().equals("已打单")).count();
                    logger.info("handle SalesOrder {}", number);
                }
                return number;
            }
        });

        Long noShipNum = 0L;
        if (result != null) {
            logger.info("result : {}", JSON.toJSONString(result));
            noShipNum = result.stream().filter(Objects::nonNull).reduce(0L, Long::sum);  
        }

        return noShipNum;
    }

    private YiyunOrderVo getYiyunOrderVo(int start, int end) {
        String from = DateUtil.getPrefixDateFormat(start);
        String to = DateUtil.getPrefixDateFormat(end);

        YiyunOrderVo oVo = new YiyunOrderVo();
        oVo.setFrom(from);
        oVo.setTo(to);

        logger.info("YiyunOrderDurring {}", JSON.toJSONString(oVo));
        return oVo;
    }

    @Override
    public <T> List<T> handleYiyunOrder(Long bizId, YiyunOrderVo orderVo, IHandleOrder<T> handleOrder)
            throws MeeException {
        List<String> dates = splitDate(orderVo);
        if (dates == null || dates.isEmpty()) {
            return null;
        }

        List<T> objects = null;

        if (dates != null && !dates.isEmpty()) {
            List<ListenableFuture<T>> futures = Lists.newArrayList();
            for (String from : dates) {
                ListenableFuture<T> task = GuavaExecutors.getDefaultCompletedExecutorService()
                        .submit(new Callable<T>() {

                            @Override
                            public T call() throws Exception {
                                YiyunOrderVo oVo = new YiyunOrderVo();
                                oVo.setFrom(from);
                                oVo.setTo(from);
                                List<YiyunOrderSales> oSales = getYiyunOrder(bizId, oVo);

                                T object = null;
                                if (handleOrder != null)
                                    object = handleOrder.handle(oSales);

                                return object;
                            }

                        });

                futures.add(task);
            }

            ListenableFuture<List<T>> resultsFuture = Futures.successfulAsList(futures);
            try {
                objects = resultsFuture.get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return objects;
    }

    private List<String> splitDate(YiyunOrderVo orderVo) {
        if (orderVo == null)
            return null;

        String from = orderVo.getFrom();
        String to = orderVo.getTo();

        Date dateFrom = DateUtil.stringToDate(from, DateUtil.formatPattern);
        Date dateTo = DateUtil.stringToDate(to, DateUtil.formatPattern);

        if (dateFrom.after(dateTo)) {
            return null;
        }

        List<String> dates = Lists.newArrayList();

        while (!dateFrom.after(dateTo)) {
            String date = DateUtil.dateToStringFormat(dateFrom, DateUtil.formatPattern);
            logger.info("Date: {}", date);
            dates.add(date);
            dateFrom = DateUtil.getSuffixDate(dateFrom, 1);
            logger.info("dateFrom: {}", dateFrom);
        }
        return dates;
    }

    @Override
    public List<OrderStatisticsData> getStatistionDatas(Long bizId, YiyunOrderDatePeriod orderVo) {
        return dataService.getDatasDay(bizId, orderVo.getFrom(), orderVo.getTo());
    }

	@Override
	public List<YiyunTopProduct> getTopProducts(Long bizId, YiyunOrderVo oVo, Integer limit) {

        List<YiyunTopProduct> topProducts = null;
        
        List<List<YiyunTopProduct>> result = handleYiyunOrder(bizId, oVo, new IHandleOrder<List<YiyunTopProduct>>() {

            @Override
            public List<YiyunTopProduct> handle(List<YiyunOrderSales> salesOrders) {
                List<YiyunTopProduct> salesOrder = Lists.newArrayList();
                if (salesOrders != null && salesOrders.size() > 0) {
                    for (YiyunOrderSales sales : salesOrders) {
                        List<YiyunOrderDetail> details = sales.getorderDetail();
                        if(details == null) 
                            continue;
                        
                        for (YiyunOrderDetail detail : details) {
                            YiyunTopProduct topProduct = new YiyunTopProduct();
                            topProduct.setSku(detail.getSku());
                            topProduct.setNumber(detail.getNumber());
                            salesOrder.add(topProduct);
                        }
                    }
                }
                return salesOrder;
            }
        });

        if(result != null && result.size() > 0) {
            List<YiyunTopProduct> products = Lists.newArrayList();
            result.stream().filter(item -> item != null && item.size() > 0).forEach(item -> products.addAll(item));

            Map<String, Double> allProduct =
            products.stream().collect(Collectors.groupingBy(YiyunTopProduct::getSku, Collectors.summingDouble(YiyunTopProduct::getNumber)));

            List<YiyunTopProduct> nProducts = Lists.newArrayList();
            for(Entry<String, Double> item : allProduct.entrySet()) {
                YiyunTopProduct tProduct = new YiyunTopProduct();
                tProduct.setSku(item.getKey());
                tProduct.setNumber(item.getValue());

                nProducts.add(tProduct);
            }
            topProducts = nProducts.stream().sorted(Comparator.comparing(YiyunTopProduct::getNumber).reversed()).limit(limit).collect(Collectors.toList());
        }

		return topProducts;
	}

	@Override
	public List<YiyunTopProduct> getTopProductsByDays(Long bizId, Integer day, Integer limit) {
        YiyunOrderVo orderVo = getYiyunOrderVo(day, 0);

		return getTopProducts(bizId, orderVo, limit);
	}

    @Override
    public List<YiyunOrderSales> getYiyunOrderByExtId(Long bizId, Set<String> extIds) {
        
        List<List<YiyunOrderSales>> datas = handleYiyunOrder(bizId, extIds, new IHandleOrder<List<YiyunOrderSales>>(){

            @Override
            public List<YiyunOrderSales> handle(List<YiyunOrderSales> salesOrders) {
                if(salesOrders == null || salesOrders.size() <= 0)
                    return null;
                return salesOrders.stream().filter(item -> item.getStatus().equals("已出库完成")).
                    collect(Collectors.toList());
            }
            
        });

        List<YiyunOrderSales> result = null;
        if(datas != null && datas.size() > 0) {
            result = new ArrayList<>();
            for(List<YiyunOrderSales> item : datas) {
                if(item != null && item.size() > 0) {
                    result.addAll(item);
                }
            }
        }
        return result;
    }

    @Override
    public <T> List<T> handleYiyunOrder(Long bizId, Set<String> extIds, IHandleOrder<T> handleOrder)
            throws MeeException {
        
                List<T> objects = null;
        
                if (extIds != null && extIds.size() > 0) {
                    List<ListenableFuture<T>> futures = Lists.newArrayList();
                    for (String extId : extIds) {
                        ListenableFuture<T> task = GuavaExecutors.getDefaultCompletedExecutorService()
                                .submit(new Callable<T>() {
        
                                    @Override
                                    public T call() throws Exception {
                                        YiyunOrderVo oVo = new YiyunOrderVo();
                                        oVo.setExternalSaleId(extId);
                                        List<YiyunOrderSales> oSales = getYiyunOrder(bizId, oVo);
        
                                        T object = null;
                                        if (handleOrder != null)
                                            object = handleOrder.handle(oSales);
        
                                        return object;
                                    }
        
                                });
        
                        futures.add(task);
                    }
        
                    ListenableFuture<List<T>> resultsFuture = Futures.successfulAsList(futures);
                    try {
                        objects = resultsFuture.get();
        
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
        
                return objects;
    }
}
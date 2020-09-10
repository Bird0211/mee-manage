package com.mee.manage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mee.manage.config.NineTeenConfig;
import com.mee.manage.exception.MeeException;
import com.mee.manage.po.PlatformConfig;
import com.mee.manage.service.INineTeenService;
import com.mee.manage.service.IOrderService;
import com.mee.manage.service.IPlatformConfigService;
import com.mee.manage.util.GuavaExecutors;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.vo.OrderDeliveryResult;
import com.mee.manage.vo.OrderItem;
import com.mee.manage.vo.OrderListResponse;
import com.mee.manage.vo.ProductVo;
import com.mee.manage.vo.Yiyun.YiyunOrderSales;
import com.mee.manage.vo.nineteen.DeliverOrders;
import com.mee.manage.vo.nineteen.DeliverResp;
import com.mee.manage.vo.nineteen.DeliveryInfo;
import com.mee.manage.vo.nineteen.DeliveryParam;
import com.mee.manage.vo.nineteen.LogisticsVo;
import com.mee.manage.vo.nineteen.NineTeenData;
import com.mee.manage.vo.nineteen.NineTeenOrder;
import com.mee.manage.vo.nineteen.NineTeenOrderDetail;
import com.mee.manage.vo.nineteen.NineTeenProduct;
import com.mee.manage.vo.nineteen.NineTeenProductGroupVo;
import com.mee.manage.vo.nineteen.NineTeenProductParam;
import com.mee.manage.vo.nineteen.NineTeenProductResponse;
import com.mee.manage.vo.nineteen.NineTeenProductResult;
import com.mee.manage.vo.nineteen.NineTeenProductTypeVo;
import com.mee.manage.vo.nineteen.NineTeenResponse;
import com.mee.manage.vo.nineteen.NineTeenSku;
import com.mee.manage.vo.nineteen.NineTeenUpdatePrice;
import com.mee.manage.vo.nineteen.NineTeenUpdateSku;
import com.mee.manage.vo.nineteen.ProductData;
import com.mee.manage.vo.nineteen.SearchVo;
import com.mee.manage.vo.nineteen.SkuInfo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * NineTeenServiceImpl
 */
@Service
public class NineTeenServiceImpl implements INineTeenService {

    protected static final Logger logger = LoggerFactory.getLogger(INineTeenService.class);

    @Autowired
    IPlatformConfigService platformService;

    @Autowired
    IOrderService orderService;

    @Autowired
    NineTeenConfig config;

    @Override
    public OrderListResponse queryOrderList(SearchVo searchVo, Integer platformId) {

        int totalCount = 0;

        List<OrderItem> items = null;
        OrderListResponse orderList = new OrderListResponse();
        List<NineTeenOrder> orders = getNineTeenOrderList(searchVo, platformId);
        if (orders != null && !orders.isEmpty()) {
            items = Lists.newArrayList();
            for (NineTeenOrder order : orders) {
                OrderItem item = new OrderItem();
                item.setAddress(order.getCollection_address());
                item.setIdCardNo(order.getIdentity_number());
                item.setName(StringUtils.isEmpty(order.getIdentity_name()) ? order.getCollection_name()
                        : order.getIdentity_name());
                int total = 0;
                item.setOrderNo(order.getTrade_no());
                item.setPhone(order.getCollection_phone());
                item.setRemark(order.getRemarks());
                item.setSender(order.getSender_name());
                item.setSenderPhone(order.getPhone());

                List<ProductVo> products = new ArrayList<>();
                for (NineTeenOrderDetail orderDetail : order.getOrder_detail()) {
                    String name = orderDetail.getName();
                    if (StringUtils.isNotEmpty(searchVo.getFilter())) {
                        if (searchVo.getFilter().equals("新西兰仓")) {
                            if (name.indexOf("国内现货") >= 0 || name.indexOf("香港一仓") >= 0) {
                                continue;
                            }
                        } else {
                            if (name.indexOf(searchVo.getFilter()) < 0) {
                                continue;
                            }
                        }
                    }

                    String skuStr = orderDetail.getCode();
                    String[] skus = null;
                    String sku = null;
                    if (StringUtils.isNotEmpty(skuStr)) {
                        skus = skuStr.split("X");
                        sku = skus[0];
                    }

                    Integer num = orderDetail.getNum();
                    if (skus != null && skus.length > 1) {
                        for (int i = 1; i < skus.length; i++) {
                            num = num * Integer.parseInt(skus[i]);
                        }
                    }
                    total += num;

                    ProductVo productVo = new ProductVo();
                    productVo.setNum(num);
                    productVo.setContent(orderDetail.getName() + ' ' + orderDetail.getSku());
                    productVo.setSku(sku);

                    products.add(productVo);
                }
                if (products != null && products.size() > 0) {
                    totalCount += total;
                    item.setProducts(products);
                    item.setNum(total);
                    items.add(item);
                }
            }
        }

        orderList.setTotalCount(totalCount);
        orderList.setPageSize(100);
        orderList.setPageNum(1);
        orderList.setItems(items);
        return orderList;
    }

    public List<NineTeenOrder> getNineTeenOrderList(SearchVo searchVo, Integer platformId) {
        if (platformId == null || platformId <= 0) {
            return null;
        }

        PlatformConfig platformConfig = platformService.getPlatFormById(platformId);
        if (platformConfig == null) {
            return null;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("account_id", platformConfig.getClientId());
        params.put("account_token", platformConfig.getToken());
        params.put("page_size", 100);
        params.put("page", 1);

        if (StringUtils.isNotBlank(searchVo.getCreateStartTime()))
            params.put("create_start_time", searchVo.getCreateStartTime());

        if (StringUtils.isNotBlank(searchVo.getCreateEndTime()))
            params.put("create_end_time", searchVo.getCreateEndTime());

        if (StringUtils.isNotBlank(searchVo.getPayStartTime()))
            params.put("pay_start_time", searchVo.getPayStartTime());

        if (StringUtils.isNotBlank(searchVo.getPayEndTime()))
            params.put("pay_end_time", searchVo.getPayEndTime());

        if (searchVo.getState() != null)
            params.put("state", searchVo.getState().toString());

        logger.info("params = {}", params);
        List<NineTeenResponse<NineTeenData>> responses = new ArrayList<>();
        postData(params, responses);

        if (responses.isEmpty()) {
            return null;
        }

        List<NineTeenOrder> result = Lists.newArrayList();
        for (NineTeenResponse<NineTeenData> nineTeenResponse : responses) {
            if (nineTeenResponse.getCode() != 200) {
                continue;
            }

            NineTeenData data = nineTeenResponse.getData();
            List<NineTeenOrder> orders = data.getData();
            if (orders != null && !orders.isEmpty())
                result.addAll(orders);
        }

        return result;
    }

    private void postData(Map<String, Object> params, List<NineTeenResponse<NineTeenData>> responses) {
        if (params == null)
            return;

        String url = config.getOrderListUrl();

        String result = JoddHttpUtils.sendPost(url, params);
        logger.info("result = {}", result);

        if (StringUtils.isEmpty(result)) {
            return;
        }

        NineTeenResponse<NineTeenData> response = null;
        try {
            response = JSON.parseObject(result, new TypeReference<NineTeenResponse<NineTeenData>>() {
            });
        } catch (Exception ex) {
            logger.error("nineteen to json errro {}", result, ex);
        }

        if (response == null || response.getCode() == null || response.getCode() != 200)
            return;

        responses.add(response);
        NineTeenData nineteenData = response.getData();
        if (nineteenData == null || nineteenData.getCurrent_page() == nineteenData.getLast_page())
            return;

        if (nineteenData.getData() == null || nineteenData.getData().isEmpty()) {
            return;
        }

        params.put("page", nineteenData.getCurrent_page() + 1);
        postData(params, responses);
    }

    @Override
    public List<NineTeenProductTypeVo> getProductType(Integer platformId, Integer typeId) {
        if (platformId == null || platformId <= 0) {
            return null;
        }

        PlatformConfig platformConfig = platformService.getPlatFormById(platformId);
        if (platformConfig == null) {
            return null;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("account_id", platformConfig.getClientId());
        params.put("account_token", platformConfig.getToken());
        params.put("pid", typeId);

        String url = config.getProductTypeUrl();
        String result = JoddHttpUtils.sendPost(url, params);
        if (StringUtils.isEmpty(result))
            return null;

        NineTeenResponse<List<NineTeenProductTypeVo>> response = JSON.parseObject(result,
                new TypeReference<NineTeenResponse<List<NineTeenProductTypeVo>>>() {
                });
        if (response == null || response.getCode() != 200)
            return null;

        return response.getData();
    }

    @Override
    public List<NineTeenProductGroupVo> getProductGroup(Integer platformId) {
        if (platformId == null || platformId <= 0) {
            return null;
        }

        PlatformConfig platformConfig = platformService.getPlatFormById(platformId);
        if (platformConfig == null) {
            return null;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("account_id", platformConfig.getClientId());
        params.put("account_token", platformConfig.getToken());

        String url = config.getProductGroupUrl();
        String result = JoddHttpUtils.sendPost(url, params);
        if (StringUtils.isEmpty(result))
            return null;

        NineTeenResponse<List<NineTeenProductGroupVo>> response = JSON.parseObject(result,
                new TypeReference<NineTeenResponse<List<NineTeenProductGroupVo>>>() {
                });
        if (response == null || response.getCode() != 200)
            return null;

        return response.getData();

    }

    @Override
    public NineTeenProductResponse getProduct(Integer platformId, NineTeenProductParam param) {
        if (platformId == null || platformId <= 0) {
            return null;
        }

        PlatformConfig platformConfig = platformService.getPlatFormById(platformId);
        if (platformConfig == null) {
            return null;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("account_id", platformConfig.getClientId());
        params.put("account_token", platformConfig.getToken());

        params.put("page_size", param.getPageSize());
        params.put("page", param.getPage());
        params.put("type_id", param.getTypeId());
        params.put("good_name", param.getGoodName());
        params.put("group_id", param.getGroupId());
        params.put("sku_code", param.getSkuCode());

        String url = config.getProductUrl();
        String result = JoddHttpUtils.sendPost(url, params);
        if (StringUtils.isEmpty(result))
            return null;

        NineTeenResponse<NineTeenProductResult> response = JSON.parseObject(result,
                new TypeReference<NineTeenResponse<NineTeenProductResult>>() {
                });

        if (response == null || response.getCode() != 200)
            return null;

        NineTeenProductResult data = response.getData();
        if (data == null)
            return null;

        List<ProductData> productData = data.getData();
        if (productData == null || productData.size() <= 0) {
            return null;
        }

        List<NineTeenProduct> nineTeenProducts = Lists.newArrayList();

        NineTeenProductResponse nineTeenResponse = new NineTeenProductResponse();
        nineTeenResponse.setPage(data.getCurrent_page());
        nineTeenResponse.setPageSize(param.getPageSize());
        nineTeenResponse.setTotal(data.getTotal());

        nineTeenResponse.setProducts(nineTeenProducts);

        for (ProductData pData : productData) {
            NineTeenProduct nineTeenProduct = new NineTeenProduct();
            nineTeenProduct.setNameCh(pData.getName_ch());
            nineTeenProduct.setNameEn(pData.getName_eh());
            nineTeenProduct.setGoodCode(pData.getCode());
            nineTeenProduct.setProductName(pData.getGood_name());

            List<SkuInfo> skuInfos = pData.getSku_info();
            List<NineTeenSku> skus = null;
            if (skuInfos != null && skuInfos.size() > 0) {
                skus = Lists.newArrayList();
                for (SkuInfo skuInfo : skuInfos) {
                    NineTeenSku sku = new NineTeenSku();
                    sku.setName(skuInfo.getSpec_name());
                    sku.setSku(skuInfo.getSku_code());
                    sku.setSkuId(skuInfo.getSku_id());
                    sku.setStock(skuInfo.getSku_stock());
                    sku.setWeight(skuInfo.getWeight());
                    sku.setPrice(skuInfo.getSku_price());
                    sku.setFirstLevel(skuInfo.getFirst_level());
                    sku.setSecondLevel(skuInfo.getSecond_level());
                    sku.setThirdLevel(skuInfo.getThird_level());
                    sku.setFourthLevel(skuInfo.getFourth_level());
                    sku.setFifthLevel(skuInfo.getFifth_level());
                    sku.setSixthLevel(skuInfo.getSixth_level());
                    skus.add(sku);
                }
            }

            nineTeenProduct.setSkuInfo(skus);
            nineTeenProducts.add(nineTeenProduct);

        }

        return nineTeenResponse;
    }

    @Override
    public boolean updatePrice(Integer platformId, List<NineTeenUpdatePrice> updatePrice) {
        if (platformId == null || platformId <= 0) {
            return false;
        }

        PlatformConfig platformConfig = platformService.getPlatFormById(platformId);
        if (platformConfig == null) {
            return false;
        }

        List<ListenableFuture<Boolean>> futures = Lists.newArrayList();

        for (NineTeenUpdatePrice price : updatePrice) {
            Map<String, Object> params = new HashMap<>();

            List<Map<String, Object>> skuInfos = Lists.newArrayList();

            params.put("account_id", platformConfig.getClientId());
            params.put("account_token", platformConfig.getToken());
            params.put("good_code", price.getGoodCode());
            params.put("sku_info", skuInfos);

            for (NineTeenUpdateSku sku : price.getSkuInfos()) {
                Map<String, Object> skuInfo = new HashMap<>();
                skuInfo.put("sku_id", sku.getSkuId());
                skuInfo.put("sku_price", sku.getSkuPrice());
                if (sku.getFirstLevel() != null) {
                    skuInfo.put("first_level", sku.getFirstLevel());
                }
                if (sku.getSecondLevel() != null) {
                    skuInfo.put("second_level", sku.getSecondLevel());
                }
                if (sku.getThirdLevel() != null) {
                    skuInfo.put("third_level", sku.getThirdLevel());
                }
                if (sku.getFourthLevel() != null) {
                    skuInfo.put("fourth_level", sku.getFourthLevel());
                }
                if (sku.getFifthLevel() != null) {
                    skuInfo.put("fifth_level", sku.getFifthLevel());
                }
                if (sku.getSixthLevel() != null) {
                    skuInfo.put("sixth_level", sku.getSixthLevel());
                }
                skuInfos.add(skuInfo);
            }

            ListenableFuture<Boolean> task = GuavaExecutors.getDefaultCompletedExecutorService()
                    .submit(new Callable<Boolean>() {

                        @Override
                        public Boolean call() throws Exception {
                            boolean flag = false;
                            try {
                                flag = updatePrice(params);
                            } catch (Exception e) {
                                logger.error("UpdatePrice Error", e);
                            }
                            return flag;
                        }

                    });

            futures.add(task);
        }

        boolean result = false;
        ListenableFuture<List<Boolean>> resultsFuture = Futures.successfulAsList(futures);
        try {
            List<Boolean> resultObj = resultsFuture.get();
            logger.info("Result: {}", resultObj);
            if (resultObj != null && resultObj.size() > 0
                    && resultObj.stream().filter(item -> item == null || item == false).count() <= 0) {
                result = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("UpdatePrice Total Error", e);

        } catch (ExecutionException e) {
            e.printStackTrace();
            logger.error("UpdatePrice Total Exp", e);

        }

        return result;
    }

    private Boolean updatePrice(Map<String, Object> params) {
        String url = config.getEditProductUrl();

        String result = JoddHttpUtils.sendPostUseBody(url, params);
        logger.info("Result: {}", result);
        if (StringUtils.isEmpty(result))
            return false;

        NineTeenResponse<String> response = JSON.parseObject(result, new TypeReference<NineTeenResponse<String>>() {
        });

        if (response == null || response.getCode() != 200)
            return false;

        logger.info(response.getData());
        return true;
    }

    @Override
    public List<DeliverOrders> deliveryList(DeliveryParam param, Integer platformId, Long bizId) throws MeeException {

        SearchVo searchVo = new SearchVo();
        searchVo.setCreateEndTime(param.getCreateEndTime());
        searchVo.setCreateStartTime(param.getCreateStartTime());
        searchVo.setState(1); // 1:待发货

        List<NineTeenOrder> response = getNineTeenOrderList(searchVo, platformId);
        if (response == null || response.isEmpty()) {
            return null;
        }

        Set<String> orderIds = response.stream().map(item -> item.getTrade_no()).collect(Collectors.toSet());

        if (orderIds == null || orderIds.size() <= 0) {
            return null;
        }

        List<YiyunOrderSales> orders = orderService.getYiyunOrderByExtId(bizId, orderIds);
        if (orders == null || orders.isEmpty()) {
            return null;
        }

        List<DeliverOrders> result = Lists.newArrayList();
        for (YiyunOrderSales order : orders) {
            List<NineTeenOrder> nineTeenOrders = response.stream()
                    .filter(item -> order.getExternalId().indexOf(item.getTrade_no().trim()) >= 0)
                    .collect(Collectors.toList());
            if (nineTeenOrders == null || nineTeenOrders.isEmpty()) {
                continue;
            }

            for (NineTeenOrder item : nineTeenOrders) {
                logger.info("Item = {}" , item);
                logger.info("Order = {}", order);
                List<NineTeenOrderDetail> details = item.getOrder_detail().stream()
                        .filter(i -> i.getCode() != null && order.getorderDetail().stream()
                                .filter(o -> o.getSku().trim().equals(i.getCode().trim())).count() > 0)
                        .collect(Collectors.toList());

                DeliverOrders o = new DeliverOrders();
                o.setAddress(item.getCollection_address());
                o.setCourierNumber(order.getLogistic().getLogisticId());
                o.setName(item.getCollection_name());
                o.setOrderDetails(details);
                o.setOrderId(item.getOrder_id().toString());
                o.setTradeNo(item.getTrade_no());
                o.setPhone(item.getCollection_phone());
                result.add(o);
            }
        }

        return result;
    }

    @Override
    public List<LogisticsVo> getLogistics(Integer platformId) {
        PlatformConfig platformConfig = platformService.getPlatFormById(platformId);
        if (platformConfig == null) {
            return null;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("account_id", platformConfig.getClientId());
        params.put("account_token", platformConfig.getToken());

        String url = config.getLogisticsUrl();

        String result = JoddHttpUtils.sendPost(url, params);

        if (StringUtils.isEmpty(result)) {
            return null;
        }

        NineTeenResponse<List<LogisticsVo>> response = null;
        try {
            response = JSON.parseObject(result, new TypeReference<NineTeenResponse<List<LogisticsVo>>>() {
            });
        } catch (Exception ex) {
            logger.error("nineteen to json errro {}", result, ex);
        }

        if (response == null) {
            return null;
        }

        return response.getData();
    }

    @Override
    public OrderDeliveryResult delivery (Integer platformId, List<DeliveryInfo> deliveryInfos) throws MeeException {
        PlatformConfig platformConfig = platformService.getPlatFormById(platformId);
        if (platformConfig == null) {
            return null;
        }

        String url = config.getDeliveryUrl();
        List<ListenableFuture<DeliverResp>> futures = Lists.newArrayList();

        for(DeliveryInfo info: deliveryInfos) {
            Map<String, Object> params = new HashMap<>();
            params.put("account_id", platformConfig.getClientId());
            params.put("account_token", platformConfig.getToken());
            params.put("order_id", info.getOrderId());
            if(info.getDetailId() != null && info.getDetailId().size() > 0)
                params.put("detail_id", info.getDetailId().stream().collect(Collectors.joining(",")));
            params.put("express_id", info.getExpressId());
            params.put("courier_number", info.getCourierNumber() );

            ListenableFuture<DeliverResp> task = GuavaExecutors.getDefaultCompletedExecutorService()
                    .submit(new Callable<DeliverResp>() {

                        @Override
                        public DeliverResp call() throws Exception {
                            boolean flag = false;
                            try {
                                flag = manualDeliver(params, url);
                            } catch (Exception e) {
                                logger.error("manualDeliver Error", e);
                            }
                            DeliverResp resp = new DeliverResp();
                            resp.setResult(flag);
                            resp.setOrderId(params.get("order_id").toString());
                            return resp;
                        }

                    });

            futures.add(task);
        }

        boolean result = false;
        List<String> errorOrderIds = null;

        ListenableFuture<List<DeliverResp>> resultsFuture = Futures.successfulAsList(futures);
        try {
            List<DeliverResp> resultObj = resultsFuture.get();
            if(resultObj != null && resultObj.size() > 0) {
                errorOrderIds = resultObj.stream().filter(item -> !item.getResult()).
                    map(item -> item.getOrderId()).collect(Collectors.toList());
                if(errorOrderIds == null || errorOrderIds.size() <= 0) {
                    result = true;
                }
            }
            logger.info("Result: {}", resultObj);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("UpdatePrice Total Error", e);

        } catch (ExecutionException e) {
            e.printStackTrace();
            logger.error("UpdatePrice Total Exp", e);
        }

        OrderDeliveryResult resp = new OrderDeliveryResult();
        resp.setSuccess(result);
        resp.setErrorOrderIds(errorOrderIds);
        return resp;
    }


    boolean manualDeliver(Map<String, Object> params ,String url) {
        String result = JoddHttpUtils.sendPost(url, params);

        if (StringUtils.isEmpty(result)) {
            return false;
        }

        NineTeenResponse<Object> response = null;
        try {
            response = JSON.parseObject(result, new TypeReference<NineTeenResponse<Object>>() {});
        } catch (Exception ex) {
            logger.error("manualDeliver to json error {}", result, ex);
        }

        if (response == null || response.getCode() != 200) {
            return false;
        } else {
            return true;
        }

    }
    

    
}
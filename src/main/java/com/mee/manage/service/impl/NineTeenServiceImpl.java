package com.mee.manage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mee.manage.config.NineTeenConfig;
import com.mee.manage.po.PlatformConfig;
import com.mee.manage.service.INineTeenService;
import com.mee.manage.service.IPlatformConfigService;
import com.mee.manage.util.GuavaExecutors;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.vo.OrderItem;
import com.mee.manage.vo.OrderListResponse;
import com.mee.manage.vo.ProductVo;
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
    NineTeenConfig config;

    @Override
    public OrderListResponse queryOrderList(SearchVo searchVo, Integer platformId) {
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

        int totalCount = 0;

        List<OrderItem> items = new ArrayList<>();
        for (NineTeenResponse<NineTeenData> nineTeenResponse : responses) {
            if (nineTeenResponse.getCode() != 200) {
                continue;
            }

            NineTeenData data = nineTeenResponse.getData();
            List<NineTeenOrder> orders = data.getData();

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

        OrderListResponse orderList = new OrderListResponse();
        orderList.setPageNum(1);
        orderList.setPageSize(100);
        orderList.setTotalCount(totalCount);
        orderList.setItems(items);
        return orderList;
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
        if(data == null)
            return null;

        List<ProductData> productData = data.getData();
        if(productData == null || productData.size() <= 0) {
            return null;
        }

        List<NineTeenProduct> nineTeenProducts = Lists.newArrayList();

        NineTeenProductResponse nineTeenResponse = new NineTeenProductResponse();
        nineTeenResponse.setPage(data.getCurrent_page());
        nineTeenResponse.setPageSize(param.getPageSize());
        nineTeenResponse.setTotal(data.getTotal());
        
        nineTeenResponse.setProducts(nineTeenProducts);

        for(ProductData pData : productData) {
            NineTeenProduct nineTeenProduct = new NineTeenProduct();
            nineTeenProduct.setNameCh(pData.getName_ch());
            nineTeenProduct.setNameEn(pData.getName_eh());
            nineTeenProduct.setGoodCode(pData.getCode());
            nineTeenProduct.setProductName(pData.getGood_name());

            List<SkuInfo> skuInfos = pData.getSku_info();
            List<NineTeenSku> skus = null;
            if(skuInfos != null && skuInfos.size() > 0) {
                skus = Lists.newArrayList();
                for(SkuInfo skuInfo : skuInfos) {
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
        
        for(NineTeenUpdatePrice price: updatePrice) {
            Map<String, Object> params = new HashMap<>();

            List<Map<String,Object>> skuInfos = Lists.newArrayList();

            params.put("account_id", platformConfig.getClientId());
            params.put("account_token", platformConfig.getToken());
            params.put("good_code", price.getGoodCode());
            params.put("sku_info", skuInfos);

            for(NineTeenUpdateSku sku : price.getSkuInfos()) {
                Map<String, Object> skuInfo = new HashMap<>();
                skuInfo.put("sku_id", sku.getSkuId());
                skuInfo.put("sku_price", sku.getSkuPrice());
                if(sku.getFirstLevel() != null) {
                    skuInfo.put("first_level", sku.getFirstLevel());
                }
                if(sku.getSecondLevel() != null) {
                    skuInfo.put("second_level", sku.getSecondLevel());
                }
                if(sku.getThirdLevel() != null) {
                    skuInfo.put("third_level", sku.getThirdLevel());
                }
                if(sku.getFourthLevel() != null) {
                    skuInfo.put("fourth_level", sku.getFourthLevel());
                }
                if(sku.getFifthLevel() != null) {
                    skuInfo.put("fifth_level", sku.getFifthLevel());
                }
                if(sku.getSixthLevel() != null) {
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
            logger.info("Result: {}" ,resultObj);
            if (resultObj != null && resultObj.size() > 0 && resultObj.stream().filter(item -> item == null || item == false).count() <= 0 ) {
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
        logger.info("Result: {}" ,result);
        if (StringUtils.isEmpty(result))
            return false;

        NineTeenResponse<String> response = JSON.parseObject(result,
                    new TypeReference<NineTeenResponse<String>>() {
                });
        
        if (response == null || response.getCode() != 200)
            return false;
        
        logger.info(response.getData());
        return true;
    }
    

    
}
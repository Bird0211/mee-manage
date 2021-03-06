package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mee.manage.config.Config;
import com.mee.manage.config.WeimobConfig;
import com.mee.manage.enums.WeimobDeliveryCompany;
import com.mee.manage.enums.WeimobOrderStatusEnum;
import com.mee.manage.exception.MeeException;
import com.mee.manage.po.Configuration;
import com.mee.manage.po.WeimobOrder;
import com.mee.manage.service.*;
import com.mee.manage.util.DateUtil;
import com.mee.manage.util.GuavaExecutors;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.*;
import com.mee.manage.vo.Yiyun.YiyunOrderSales;
import com.mee.manage.vo.weimob.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class WeimobServiceImpl implements IWeimobService {

    private static final Logger logger = LoggerFactory.getLogger(IWeimobService.class);

    @Autowired
    IConfigurationService configurationService;

    @Autowired
    WeimobConfig weimobConfig;

    @Autowired
    IProductsService productsService;

    @Autowired
    IWeimobOrderService weimobOrderService;

    @Autowired
    IExpressService expressService;

    @Autowired
    IkdnService kdnService;

    @Autowired
    IOrderService orderService;

    @Override
    public boolean addCode(String code, Long bizId) {
        if (code == null)
            return false;

        Map<String, Object> data = new HashMap<>();
        data.put("code", code);
        data.put("grant_type", "authorization_code");
        data.put("client_id", weimobConfig.getClientId());
        data.put("client_secret", weimobConfig.getClientSecret());
        data.put("redirect_uri", weimobConfig.getReturnUri());

        logger.info(JSON.toJSONString(data));
        String result = JoddHttpUtils.sendPost(weimobConfig.getWeimobTokenUrl(), data);
        logger.info(result);
        WeimobTokenResponse weimobTokenResponse = JSON.parseObject(result, WeimobTokenResponse.class);
        return saveToken(weimobTokenResponse, bizId);
    }

    @Override
    public CheckTokenResult checkToken(Long bizId) {
        boolean flag = false;
        String token = null;
        Configuration tokenConfig = configurationService.getConfig(Config.WEIMOBTOKEN + "_" + bizId);
        if (tokenConfig != null) {
            if (tokenConfig.getExpir().after(DateUtil.getSuffixMinute(30))) { // token > new date()
                flag = true;
                token = tokenConfig.getValue();
                logger.info("Token is Available Token = {}, Expire = {}", 
                        token, 
                        DateUtil.dateToStringFormat(tokenConfig.getExpir()), DateUtil.formatPattern_24Full);
            } else {
                Configuration refreshToken = configurationService.getConfig(Config.WEIMOBREREFRESHTOKEN + "_" + bizId);
                if(refreshToken == null) {
                    logger.info("Refresh Token is null"); 
                } else {
                    logger.info("Refresh Token = {}, Refresh Expire = {}", 
                        DateUtil.dateToStringFormat(refreshToken.getExpir(), DateUtil.formatPattern_24Full));

                    if (refreshToken.getExpir().after(new Date())) { // refreshToken > new date()
                        return refreshToken(refreshToken.getValue(), bizId);
                    } else {
                        logger.info("Refresh Token is Expire"); 
                    }
                }
            }
        }

        CheckTokenResult tokenResult = new CheckTokenResult();
        tokenResult.setCuccess(flag);
        tokenResult.setToken(token);
        return tokenResult;
    }

    @Override
    public String getToken(Long bizId) {
        CheckTokenResult tokenResult = checkToken(bizId);
        if (tokenResult == null || !tokenResult.isCuccess()) {
            logger.info("Token Error");
            return null;
        }

        String token = tokenResult.getToken();
        logger.info("Token = {}", token);
        return token;
    }

    @Override
    public CheckTokenResult refreshToken(String refreshToken, Long bizId) {
        CheckTokenResult tokenResult = new CheckTokenResult();
        if (refreshToken == null) {
            tokenResult.setCuccess(false);
            return tokenResult;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("grant_type", "refresh_token");
        data.put("client_id", weimobConfig.getClientId());
        data.put("client_secret", weimobConfig.getClientSecret());
        data.put("refresh_token", refreshToken);
        data.put("redirect_uri", weimobConfig.getReturnUri());

        String result = JoddHttpUtils.sendPost(weimobConfig.getWeimobTokenUrl(), data);
        WeimobTokenResponse weimobTokenResponse = JSON.parseObject(result, WeimobTokenResponse.class);

        if (weimobTokenResponse == null) {
            tokenResult.setCuccess(false);
            return tokenResult;
        }
        boolean isSaveToken = saveToken(weimobTokenResponse, bizId);
        tokenResult.setCuccess(isSaveToken);
        if (isSaveToken) {
            tokenResult.setToken(weimobTokenResponse.getAccess_token());
        }
        return tokenResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setToken(String token, Date expire, String refreshToken, Date expireRefreshToken, Long bizId) {
        if (token == null || refreshToken == null || expire.before(new Date()) || expireRefreshToken.before(new Date()))
            return false;

        boolean flag = false;
        logger.info("saveToken Token: {}; Expire: {}", 
            token, 
            DateUtil.dateToStringFormat(expire, DateUtil.formatPattern_24Full));
        logger.info("saveRefreshToken : Token {}; RefreshExpire: {}", 
            refreshToken, 
            DateUtil.dateToStringFormat(expireRefreshToken, DateUtil.formatPattern_24Full));

        // 入库、事务
        try {
            Configuration tokenConfig = configurationService.getConfig(Config.WEIMOBTOKEN + "_" + bizId);
            if (tokenConfig == null)
                configurationService.insertConfig(Config.WEIMOBTOKEN + "_" + bizId, token, expire);
            else
                configurationService.updateConfig(Config.WEIMOBTOKEN + "_" + bizId, token, expire);

            Configuration reFreshTokenConfig = configurationService
                    .getConfig(Config.WEIMOBREREFRESHTOKEN + "_" + bizId);
            if (reFreshTokenConfig == null)
                configurationService.insertConfig(Config.WEIMOBREREFRESHTOKEN + "_" + bizId, refreshToken,
                        expireRefreshToken);
            else
                configurationService.updateConfig(Config.WEIMOBREREFRESHTOKEN + "_" + bizId, refreshToken,
                        expireRefreshToken);

            flag = true;
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("set Weimob Token Err ", ex);
            flag = false;
        }

        return flag;
    }

    @Override
    public WeimobOrderListResponse getOrderList(WeimobOrderListRequest request, Long bizId) throws MeeException {
        if (request == null) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }
        List<WeimobOrderData> datas = getOrderData(request, bizId);

        return getOrderListData(datas, request.getSendarea(), request.getOrderType(),
                request.getFlagRanks(), bizId);
    }

    @Override
    public WeimobOrderListRespVo getList(WeimobOrderListRequest request, Long bizId) {
        if (request == null) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }
        List<WeimobOrderData> datas = getOrderData(request, bizId);
        if(datas == null || datas.isEmpty()) {
            return null;
        }

        return getListData(datas, request.getSendarea(), request.getOrderType(),
                request.getFlagRanks(), bizId);
    }


    public List<WeimobOrderData> getOrderData(WeimobOrderListRequest request, Long bizId) throws MeeException {
        CheckTokenResult tokenResult = checkToken(bizId);
        if (tokenResult == null || !tokenResult.isCuccess()) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }

        String token = tokenResult.getToken();
        int pageNum = 1;
        int pageSize = 100;
        int totalCount = 0;
        
        List<WeimobOrderData> datas = new ArrayList<>();
        StatusCode statusCode = null;
        WeimobOrderRequest orderRequest = new WeimobOrderRequest();
        String url = weimobConfig.getWeimobOrderListUrl() + "?accesstoken=" + token;

        do {
            // 代码语句
            orderRequest.setPageNum(pageNum);
            orderRequest.setPageSize(pageSize);

            WeimobQueryParameter parameter = new WeimobQueryParameter();
            parameter.setCreateStartTime(request.getCreateStartTime().getTime());
            parameter.setCreateEndTime(request.getCreateEndTime().getTime());
            if (request.getOrderStatuses() != null)
                parameter.setOrderStatuses(new Integer[] { request.getOrderStatuses() });

            if (request.getSendarea() != null) {
                if (request.getSendarea() == 0)
                    parameter.setKeyword("新西兰仓");
                else if (request.getSendarea() == 1) {
                    parameter.setKeyword("国内现货");
                }
                parameter.setSearchType(1);// 搜索类型 (1商品名称，2商品编码，3客户昵称，4订单编号，5收货人姓名，6收货人手机号，7交易单号，8商户单号，9提货码，99多字段搜索)
            }

            if (request.getFlagRanks() != null && request.getFlagRanks() > 0) {
                parameter.setFlagRanks(new Integer[] { request.getFlagRanks() });
            }
            orderRequest.setQueryParameter(parameter);
            String result = JoddHttpUtils.sendPostUseBody(url, orderRequest);
            if (result == null) {
                statusCode = StatusCode.SYS_ERROR;
                break;
            }

            if (result.indexOf("80001001000119") >= 0) {
                statusCode = StatusCode.WEIMOB_TOKEN_ERROR;
                configurationService.removeConfig(Config.WEIMOBTOKEN + '_' + bizId);
                configurationService.removeConfig(Config.WEIMOBREREFRESHTOKEN + '_' + bizId);

                break;
            }

            WeimobOrderResponse orderResponse = JSON.parseObject(result, WeimobOrderResponse.class,
                    Feature.IgnoreNotMatch);
            if (orderResponse == null) {
                statusCode = StatusCode.SYS_ERROR;
                break;
            }

            WeimobOrderCode code = orderResponse.getCode();
            if (code == null) {
                statusCode = StatusCode.SYS_ERROR;
                break;
            }

            if (code.getErrcode().equals("80001001000119")) {
                statusCode = StatusCode.WEIMOB_TOKEN_ERROR;
                break;
            }

            if (code.getErrcode().equals("0")) {
                WeimobOrderData data = orderResponse.getData();
                totalCount = data.getTotalCount();
                datas.add(data);
                statusCode = StatusCode.SUCCESS;
            } else {
                statusCode = StatusCode.WEIMOB_TOKEN_ERROR;
                break;
            }
            pageNum++;
        } while ((pageNum - 1) * pageSize < totalCount);

        if (statusCode != StatusCode.SUCCESS) {
            throw new MeeException(statusCode);
        }
        return datas;
    }
    

    @Override
    public List<WeimobGroupVo> getClassifyInfo(Long bizId) {
        String token = getToken(bizId);
        if (token == null)
            return null;

        String url = weimobConfig.getGoodsClassifyUrl() + "?accesstoken=" + token;
        String result = JoddHttpUtils.sendPost(url, null);
        if (result == null || result.isEmpty())
            return null;

        logger.info(result);
        WeimobGoodsClassifyResponse goodsClassifyResponse = JSON.parseObject(result, WeimobGoodsClassifyResponse.class);

        if (goodsClassifyResponse == null)
            return null;

        WeimobOrderCode code = goodsClassifyResponse.getCode();
        if (code == null || !code.getErrcode().equals("0"))
            return null;

        WeimobGoodsClassifyData data = goodsClassifyResponse.getData();
        if (data == null)
            return null;

        List<GoodsClassify> goodsClassifies = data.getGoodsClassifyList();
        if (goodsClassifies == null || goodsClassifies.isEmpty())
            return null;

        return getGroupList(goodsClassifies);
    }

    @Override
    public WeimobOrderDetailVo getWeimobOrder(String orderId, Long bizId) {
        if (orderId == null) {
            logger.info("OrderId is null");
            return null;
        }


        String token = getToken(bizId);
        if (token == null) {
            logger.info("Token is null bizId {}", bizId);
            return null;
        }


        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderId);
        params.put("needInvoiceInfo", false);
        params.put("needMemberInfo", false);

        String url = weimobConfig.getOrderDetail() + "?accesstoken=" + token;
        logger.info(JSON.toJSONString(params));
        String result = JoddHttpUtils.sendPostUseBody(url, params);
        if (result == null || result.isEmpty()) {

            return null;
        }

        logger.info("WeimobOrder = {}", result);

        WeimobOrderDetailVo orderDetail = null;
        WeimobOrderVo weimobOrder = JSON.parseObject(result, WeimobOrderVo.class, Feature.IgnoreNotMatch);
        if (weimobOrder != null && weimobOrder.getCode().getErrcode().equals("0")) {
            orderDetail = weimobOrder.getData();
        }
        return orderDetail;
    }

    @Override
    public List<GoodPageList> getGoodList(GoodListQueryParameter params, Long bizId) {

        String token = getToken(bizId);
        if (StringUtils.isEmpty(token))
            return null;

        GoodListRequest goodListRequest = new GoodListRequest();
        goodListRequest.setQueryParameter(params);
        String url = weimobConfig.getGoodListUrl() + "?accesstoken=" + token;
        int pageNum = 1;
        int pageSize = 20;
        int totalCount = 0;
        List<GoodPageList> pageList = new ArrayList<>();

        do {
            goodListRequest.setPageNum(pageNum);
            goodListRequest.setPageSize(pageSize);

            String result = JoddHttpUtils.sendPostUseBody(url, goodListRequest);
            if (result == null || result.isEmpty())
                break;

            logger.info("QueryGoodList = {}", result);
            GoodListResponse goodListResponse = JSON.parseObject(result, GoodListResponse.class);
            if (goodListResponse == null)
                break;

            WeimobOrderCode code = goodListResponse.getCode();
            if (code == null)
                break;

            if (code.getErrcode() == null || !code.getErrcode().equals("0"))
                break;

            GoodListData data = goodListResponse.getData();
            totalCount = data.getTotalCount();

            pageList.addAll(data.getPageList());

            pageNum++;

        } while ((pageNum - 1) * pageSize < totalCount);

        return pageList;
    }

    @Override
    public List<GoodInfoVo> getWeimobGoods(GoodListQueryParameter params, Long bizId) {

        List<GoodPageList> goodList = getGoodList(params, bizId);
        Map<String, MeeProductVo> allProducts = productsService.getMapMeeProduct("20");

        List<GoodInfoVo> goodInfos = new ArrayList<>();

        if (goodList != null && !goodList.isEmpty()) {
            List<ListenableFuture<GoodDetailData>> futures = Lists.newArrayList();
            for (GoodPageList good : goodList) {
                ListenableFuture<GoodDetailData> task = GuavaExecutors.getDefaultCompletedExecutorService()
                        .submit(new Callable<GoodDetailData>() {

                            @Override
                            public GoodDetailData call() throws Exception {
                                GoodDetailData goodData = getWeimobGoodDetail(good.getGoodsId(), bizId);
                                return goodData;
                            }

                        });

                futures.add(task);
            }

            ListenableFuture<List<GoodDetailData>> resultsFuture = Futures.successfulAsList(futures);
            try {
                List<GoodDetailData> datas = resultsFuture.get();
                if (datas != null && !datas.isEmpty()) {
                    for (GoodDetailData goodDetailData : datas) {
                        List<GoodInfoVo> finalGoodInfos = stGoodInfo(goodDetailData, allProducts);
                        goodInfos.addAll(finalGoodInfos);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return goodInfos;
    }

    private List<GoodInfoVo> stGoodInfo(GoodDetailData goodData, Map<String, MeeProductVo> allProducts) {
        List<GoodInfoVo> goodInfos = null;
        if (goodData != null) {

            GoodDetailVo goodDetail = goodData.getGoods();
            if (goodDetail == null)
                return null;

            goodInfos = new ArrayList<>();
            List<WeimobSkuVo> skuList = goodDetail.getSkuList();
            String defaultImg = goodDetail.getDefaultImageUrl();
            if (skuList != null && !skuList.isEmpty()) {
                for (WeimobSkuVo skuVo : skuList) {
                    GoodInfoVo goodInfo = getGoodInfo(skuVo, allProducts);
                    if (goodInfo != null) {
                        if (StringUtils.isEmpty(goodInfo.getDefaultImageUrl()))
                            goodInfo.setDefaultImageUrl(defaultImg);

                        if (StringUtils.isEmpty(goodInfo.getDefaultImageUrl())) {
                            List<String> images = goodDetail.getGoodsImageUrl();
                            if (images != null && images.size() > 0) {
                                goodInfo.setDefaultImageUrl(images.get(0));
                            }
                        }
                        goodInfos.add(goodInfo);
                    }
                }
            }

        }

        return goodInfos;
    }

    private GoodInfoVo getGoodInfo(WeimobSkuVo skuVo, Map<String, MeeProductVo> allProducts) {
        if (skuVo == null || allProducts == null || allProducts.isEmpty())
            return null;
        GoodInfoVo goodInfo = new GoodInfoVo();

        Map<String, SkuAttrMap> skuAttrMap = skuVo.getSkuAttrMap();
        StringBuffer sb = new StringBuffer(skuVo.getProductTitle());
        if (skuAttrMap != null) {
            Set<String> keys = skuAttrMap.keySet();
            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    SkuAttrMap attrMap = skuAttrMap.get(key);
                    sb.append("[").append(attrMap.getName()).append(":").append(attrMap.getValue()).append("]");
                }
            }
        }

        if (sb != null && sb.length() > 0) {
            goodInfo.setTitle(sb.toString());

        }

        if (!StringUtils.isEmpty(skuVo.getImageUrl())) {
            goodInfo.setDefaultImageUrl(skuVo.getImageUrl());
        }

        if (skuVo.getGoodsId() != null && skuVo.getGoodsId() != 0) {
            goodInfo.setGoodsId(skuVo.getGoodsId());

        }

        goodInfo.setSalesPrice(skuVo.getSalePrice());
        goodInfo.setCostPrice(skuVo.getCostPrice());
        goodInfo.setSkuId(skuVo.getSkuId());
        goodInfo.setOriginalPrice(skuVo.getOriginalPrice());

        String outerSky = skuVo.getOuterSkuCode();
        if (!StringUtils.isEmpty(outerSky) && !StringUtils.isEmpty(skuVo.getOuterSkuCode()))
            goodInfo.setSku(skuVo.getOuterSkuCode().split("_")[0]);

        MeeProductVo meeProduct = allProducts.get(goodInfo.getSku());
        if (meeProduct != null) {
            goodInfo.setYiyunCostPrice(meeProduct.getCostPrice());
            goodInfo.setYiyunSalesPrice(meeProduct.getRetailPrice());
            goodInfo.setWeight(meeProduct.getWeight());
        }

        return goodInfo;

    }

    @Override
    public GoodDetailData getWeimobGoodDetail(Long goodId, Long bizId) {
        if (goodId == null)
            return null;

        String token = getToken(bizId);
        String url = weimobConfig.getGoodDetailUrl() + "?accesstoken=" + token;
        Map<String, Object> params = new HashMap<>();
        params.put("goodsId", goodId);
        String result = JoddHttpUtils.sendPostUseBody(url, params);
        if (result == null || result.isEmpty())
            return null;

        logger.info(result);
        GoodDetailResponse detailResponse = JSON.parseObject(result, GoodDetailResponse.class);
        if (detailResponse == null)
            return null;

        WeimobOrderCode code = detailResponse.getCode();
        if (code == null)
            return null;

        if (code.getErrcode() == null || !code.getErrcode().equals("0"))
            return null;

        GoodDetailData goodDetail = detailResponse.getData();
        return goodDetail;
    }

    @Override
    public List<PriceUpdateResult> updateWeimobPrice(List<GoodPriceDetail> goodsPrice, Long bizId) {
        if (goodsPrice == null || goodsPrice.isEmpty())
            return null;

        Map<Long, List<GoodPriceDetail>> mergeGoods = new HashMap<>();
        goodsPrice.forEach((item) -> {
            List<GoodPriceDetail> goods = mergeGoods.get(item.getGoodId());
            if (goods == null) {
                goods = Lists.newArrayList();
            }
            goods.add(item);

            mergeGoods.put(item.getGoodId(), goods);
        });

        List<ListenableFuture<List<PriceUpdateResult>>> futures = Lists.newArrayList();
        Set<Long> keys = mergeGoods.keySet();
        for (Long key : keys) {
            List<GoodPriceDetail> goodPrices = mergeGoods.get(key);
            if (goodPrices == null || goodPrices.isEmpty())
                continue;

            ListenableFuture<List<PriceUpdateResult>> task = GuavaExecutors.getDefaultCompletedExecutorService()
                    .submit(new Callable<List<PriceUpdateResult>>() {

                        @Override
                        public List<PriceUpdateResult> call() throws Exception {
                            List<PriceUpdateResult> priceUpdate = updatePrice(key, goodPrices, bizId);

                            return priceUpdate;
                        }

                    });
            futures.add(task);
        }

        List<PriceUpdateResult> priceUpdateResults = new ArrayList<>();
        ListenableFuture<List<List<PriceUpdateResult>>> resultsFuture = Futures.successfulAsList(futures);
        try {
            List<List<PriceUpdateResult>> updateResult = resultsFuture.get();
            for (List<PriceUpdateResult> results : updateResult)
                if (results != null && results.size() > 0)
                    priceUpdateResults.addAll(results);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return priceUpdateResults;
    }

    @Override
    public GoodInfoVo getWeimobGoodBySku(Long sku, Long bizId) {
        if (sku == null || sku <= 0)
            return null;

        WeimobOrder weimobOrder = weimobOrderService.getWeimboOrders(sku);
        if (weimobOrder == null)
            return null;

        GoodInfoVo infoVo = null;

        Long goodId = weimobOrder.getGoodId();
        List<WeimobSkuVo> skuVos = getSkuList(goodId, bizId);

        if (skuVos != null && !skuVos.isEmpty()) {
            for (WeimobSkuVo skuVo : skuVos) {
                if (skuVo.getOuterSkuCode().equals(sku.toString())) {
                    Map<String, MeeProductVo> allProducts = productsService.getMapMeeProduct("20");
                    infoVo = getGoodInfo(skuVo, allProducts);
                    break;
                }
            }
        }

        return infoVo;
    }

    @Override
    public boolean refreshWeimob(Long bizId) {

        List<GoodInfoVo> goods = getWeimobGoods(null, bizId);
        if (goods == null || goods.isEmpty())
            return false;

        List<ListenableFuture<Boolean>> futures = Lists.newArrayList();
        for (GoodInfoVo goodInfo : goods) {
            ListenableFuture<Boolean> task = GuavaExecutors.getDefaultCompletedExecutorService()
                    .submit(new Callable<Boolean>() {

                        @Override
                        public Boolean call() throws Exception {
                            boolean flag = false;
                            WeimobOrder order = weimobOrderService.getWeimobOrder(Long.parseLong(goodInfo.getSku()),
                                    goodInfo.getGoodsId());
                            if (order == null) {
                                WeimobOrder weimobOrder = new WeimobOrder();
                                weimobOrder.setGoodId(goodInfo.getGoodsId());
                                weimobOrder.setSku(Long.parseLong(goodInfo.getSku()));
                                flag = weimobOrderService.addWeimobOrder(order);
                            }
                            return flag;
                        }

                    });

            futures.add(task);
        }
        ListenableFuture<List<Boolean>> resultsFuture = Futures.successfulAsList(futures);
        try {
            List<Boolean> datas = resultsFuture.get();
            if (datas != null && !datas.isEmpty()) {
                for (boolean flag : datas) {
                    if (!flag) {
                        logger.info("Refresh fail");
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public OrderDeliveryResult orderDelivery(List<DeliveryOrderVo> deleverOrders, Long bizId) {
        OrderDeliveryResult result = new OrderDeliveryResult();
        if (deleverOrders == null || deleverOrders.isEmpty()) {
            logger.info("DeleverOrders params is null");
            result.setSuccess(false);
            return result;
        }

        DeliveryOrderSplit splitOrder = splitDelivery(deleverOrders);
        if (splitOrder == null) {
            logger.info("SplitOrder result is null");
            result.setSuccess(false);
            return result;
        }
        List<String> error = new ArrayList<>();
        if (splitOrder.getDeleverBatchOrders() != null && splitOrder.getDeleverBatchOrders().size() > 0) {
            boolean bathResult = sendBathOrder(splitOrder.getDeleverBatchOrders(), bizId);
            if (!bathResult) {
                error.addAll(getErrorOrderId(splitOrder.getDeleverBatchOrders()));
            }
        }

        if (splitOrder.getDeleverSingleOrders() != null && splitOrder.getDeleverSingleOrders().size() > 0) {
            List<DeliveryOrderVo> signleResult = sendSingleOrder(splitOrder.getDeleverSingleOrders(), bizId);
            if (signleResult != null && !signleResult.isEmpty()) {
                error.addAll(getErrorOrderId(signleResult));
            }
        }

        if (error == null || error.isEmpty())
            result.setSuccess(true);

        else {
            result.setSuccess(false);
            result.setErrorOrderIds(error);
        }
        return result;
    }

    @Override
    public boolean sendBathOrder(List<DeliveryOrderVo> deleverOrders, Long bizId) {
        if (deleverOrders == null || deleverOrders.size() <= 0) {
            logger.info("Batch Order deleverOrders is null!");
            return false;
        }

        logger.info("BathOrder = {}", deleverOrders);

        String token = getToken(bizId);
        String url = weimobConfig.getBatchDeliveryUrl() + "?accesstoken=" + token;

        WeimobBatchDeliveryRequest params = new WeimobBatchDeliveryRequest();
        List<WeimobBatchDelivery> deliveryOrderList = new ArrayList<>();
        for (DeliveryOrderVo deliveryOrder : deleverOrders) {
            WeimobDeliveryCompany deliveryCom = null;
            if (StringUtils.isEmpty(deliveryOrder.getExpressComCode())) {
                if (deliveryOrder.getDeliveryId().startsWith("7")) {
                    deliveryCom = WeimobDeliveryCompany.shunfeng;
                } else if (deliveryOrder.getDeliveryId().startsWith("1")) {
                    deliveryCom = WeimobDeliveryCompany.ftd;
                }
            } else {
                deliveryCom = WeimobDeliveryCompany.getExpCompanyByCode(deliveryOrder.getExpressComCode());
            }
            if (deliveryCom == null) {
                logger.info("Delivery not suport!");
                continue;
            }
            WeimobBatchDelivery batchDelivery = new WeimobBatchDelivery();
            batchDelivery.setOrderNo(Long.parseLong(deliveryOrder.getOrderId()));
            batchDelivery.setDeliveryNo(deliveryOrder.getDeliveryId());
            batchDelivery.setDeliveryCompanyCode(deliveryCom.getCode());
            batchDelivery.setDeliveryCompanyName(deliveryCom.getName());
            batchDelivery.setNeedLogistics(true);

            deliveryOrderList.add(batchDelivery);
        }

        if(deliveryOrderList == null || deliveryOrderList.size() <= 0) {
            return false;
        }
        params.setDeliveryOrderList(deliveryOrderList);

        String result = JoddHttpUtils.sendPostUseBody(url, params);
        logger.info("sendBathOrder result = {}", result);
        if (result == null || result.isEmpty())
            return false;

        WeimobDeliveryOrderResp resp = JSON.parseObject(result, WeimobDeliveryOrderResp.class);
        if (resp == null || resp.getData() == null)
            return false;
        else
            return resp.getData().getSuccess();
    }

    @Override
    public List<DeliveryOrderVo> sendSingleOrder(List<DeliveryOrderVo> deleverOrders, Long bizId) {
        if (deleverOrders == null || deleverOrders.isEmpty()) {
            logger.info("Single Order deleverOrders is null!");
            return null;
        }

        logger.info("SingleOrder = {}", deleverOrders);

        String token = getToken(bizId);
        String url = weimobConfig.getOrderDeliveryUrl() + "?accesstoken=" + token;

        List<DeliveryOrderVo> errorResult = new ArrayList<>();

        List<ListenableFuture<WeimobDeliveryOrderResp>> futures = Lists.newArrayList();
        for (DeliveryOrderVo order : deleverOrders) {

            ListenableFuture<WeimobDeliveryOrderResp> task = GuavaExecutors.getDefaultCompletedExecutorService()
                    .submit(new Callable<WeimobDeliveryOrderResp>() {

                        @Override
                        public WeimobDeliveryOrderResp call() throws Exception {
                            return sendSingleOrder(order, url);
                        }

                    });
            futures.add(task);
        }

        List<WeimobDeliveryOrderResp> resultsFutures = null;
        ListenableFuture<List<WeimobDeliveryOrderResp>> resultsFuture = Futures.successfulAsList(futures);
        try {
            resultsFutures = resultsFuture.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for (WeimobDeliveryOrderResp resp : resultsFutures) {
            if (resp == null || resp.getCode() == null || resp.getData() == null || !resp.getData().getSuccess()) {
                errorResult.add(resp.getDeliveryOrder());
            }
        }

        return errorResult;
    }

    @Override
    public WeimobDeliveryOrderResp sendSingleOrder(DeliveryOrderVo deleverOrder, Long bizId) {
        String token = getToken(bizId);
        String url = weimobConfig.getOrderDeliveryUrl() + "?accesstoken=" + token;
        return sendSingleOrder(deleverOrder, url);
    }

    public WeimobDeliveryOrderResp sendSingleOrder(DeliveryOrderVo deliveryOrder, String url) {
        if(deliveryOrder == null ) {
            logger.info("deliveryOrder is null!");
            WeimobDeliveryOrderResp errorResp = new WeimobDeliveryOrderResp();
            errorResp.setDeliveryOrder(deliveryOrder);
            return errorResp;
        }
        WeimobSingleRequest request = new WeimobSingleRequest();
        WeimobDeliveryCompany deliveryCom = WeimobDeliveryCompany.getExpCompanyByCode(deliveryOrder.getExpressComCode());

        if (deliveryCom == null) {
            logger.info("sendSigleOrder deliveryCom is null!");
            WeimobDeliveryOrderResp errorResp = new WeimobDeliveryOrderResp();
            errorResp.setDeliveryOrder(deliveryOrder);
            return errorResp;
        }

        request.setOrderNo(Long.parseLong(deliveryOrder.getOrderId().split("-")[0]));
        request.setDeliveryNo(deliveryOrder.getDeliveryId());
        request.setDeliveryOrderId(null);
        request.setDeliveryCompanyCode(deliveryCom.getCode());
        request.setDeliveryCompanyName(deliveryCom.getName());
        request.setDeliveryRemark(null);
        request.setIsNeedLogistics(true);
        request.setIsSplitPackage(true);
        
        List<WeimobSingleSku> skus = null;
        List<DeliverySkuInfo> skuInfos = deliveryOrder.getSkuInfo();
        if(skuInfos != null) {
            for (DeliverySkuInfo skuInfo : skuInfos) {
                WeimobSingleSku singleSku = new WeimobSingleSku();
    
                singleSku.setSkuId(skuInfo.getSkuId());
                singleSku.setItemId(skuInfo.getItemId());
                singleSku.setSkuNum(skuInfo.getSkuNum());
                if (skus == null)
                    skus = new ArrayList<>();
                skus.add(singleSku);
            }
        }

        if (skus == null || skus.size() <= 0) {
            logger.info("sendSigleOrder SKU is null!");

            WeimobDeliveryOrderResp errorResp = new WeimobDeliveryOrderResp();
            errorResp.setDeliveryOrder(deliveryOrder);
            return errorResp;
        }

        request.setDeliveryOrderItemList(skus);

        String result = JoddHttpUtils.sendPostUseBody(url, request);
        logger.info("sendSigleOrder result = {}", result);
        if (result == null || result.isEmpty()) {
            WeimobDeliveryOrderResp errorResp = new WeimobDeliveryOrderResp();
            errorResp.setDeliveryOrder(deliveryOrder);
            return errorResp;
        }

        WeimobDeliveryOrderResp resp = JSON.parseObject(result, WeimobDeliveryOrderResp.class);
        resp.setDeliveryOrder(deliveryOrder);
        return resp;
    }

    private DeliveryOrderSplit splitDelivery(List<DeliveryOrderVo> deleverOrders) {
        if (deleverOrders == null || deleverOrders.isEmpty())
            return null;

        List<DeliveryOrderVo> deleverBatchOrders = deleverOrders.stream().filter(item -> !item.isSplit()).collect(Collectors.toList());
        List<DeliveryOrderVo> deleverSingleOrders = deleverOrders.stream().filter(item -> item.isSplit()).collect(Collectors.toList());

        DeliveryOrderSplit splitOrder = new DeliveryOrderSplit();
        splitOrder.setDeleverBatchOrders(deleverBatchOrders);
        splitOrder.setDeleverSingleOrders(deleverSingleOrders);

        return splitOrder;
    }



    private List<WeimobSkuVo> getSkuList(Long goodId, Long bizId) {
        if (goodId == null || goodId <= 0)
            return null;

        GoodDetailData goodDetail = getWeimobGoodDetail(goodId, bizId);
        if (goodDetail == null)
            return null;

        GoodDetailVo detailVo = goodDetail.getGoods();
        if (detailVo == null)
            return null;

        List<WeimobSkuVo> skuVos = detailVo.getSkuList();
        if (skuVos == null || skuVos.isEmpty())
            return null;

        return skuVos;
    }

    private List<WeimobGroupVo> getGroupList(List<GoodsClassify> goodsClassifies) {
        if (goodsClassifies == null || goodsClassifies.isEmpty())
            return null;

        List<WeimobGroupVo> groupVos = new ArrayList<>();
        for (GoodsClassify goodsClassify : goodsClassifies) {
            if (goodsClassify == null)
                continue;
            WeimobGroupVo weimobGroupVo = new WeimobGroupVo();
            String title = goodsClassify.getTitle();
            int level = goodsClassify.getLevel();
            Long classifyId = goodsClassify.getClassifyId();

            weimobGroupVo.setClassifyId(classifyId);
            weimobGroupVo.setLevel(level);
            weimobGroupVo.setTitle(title);

            List<WeimobGroupVo> childrenGroup = getGroupList(goodsClassify.getChildrenClassify());
            if (childrenGroup != null && childrenGroup.size() > 0)
                weimobGroupVo.setChildrenGroup(childrenGroup);

            groupVos.add(weimobGroupVo);
        }

        return groupVos;
    }

    private boolean saveToken(WeimobTokenResponse refreshTokenResponse, Long bizId) {
        if (refreshTokenResponse == null)
            return false;

        String token = refreshTokenResponse.getAccess_token();
        String reToken = refreshTokenResponse.getRefresh_token();
        int expireToken = refreshTokenResponse.getExpires_in();
        int expireRefreshToken = refreshTokenResponse.getRefresh_token_expires_in();

        return setToken(token, DateUtil.getSuffixSecond(expireToken), reToken,
                DateUtil.getSuffixSecond(expireRefreshToken), bizId);
    }

    private WeimobOrderListResponse getOrderListData(List<WeimobOrderData> datas, Integer sendArea, Integer orderType,
            Integer flagFrank, Long bizId) {
        if (datas == null || datas.isEmpty() || datas.size() <= 0)
            return null;

        String filterTxt = null;
        if (sendArea != null) {
            if (sendArea == 0) {
                filterTxt = "新西兰仓";
            } else if (sendArea == 1) {
                filterTxt = "国内现货";
            }
        }

        List<Long> milkIds = null;
        if (orderType != null) {
            milkIds = getMilkIds(bizId);
        }

        Map<String, MeeProductVo> allProducts = productsService.getMapMeeProduct(bizId.toString());

        WeimobOrderListResponse response = new WeimobOrderListResponse();
        List<ListenableFuture<WeimobOrderDetailVo>> futures = Lists.newArrayList();

        List<WeimobItemsResponse> weimobItems = new ArrayList<>();

        for (int i = 0; i < datas.size(); i++) {
            WeimobOrderData data = datas.get(i);
            if (i == 0) {
                response.setPageNum(data.getPageNum());
                response.setPageSize(data.getPageSize());
                response.setTotalCount(data.getTotalCount());
            }
            List<WeimobOrderDataList> items = data.getPageList();
            if (items != null && items.size() > 0) {
                for (WeimobOrderDataList item : items) {
                    String address = item.getReceiverAddress();
                    String mobile = item.getReceiverMobile();
                    String name = item.getReceiverName();
                    Long orderNo = item.getOrderNo();
                    String sender = item.getUserNickname();

                    List<WeimobItem> products = item.getItemList();
                    StringBuffer content = new StringBuffer();
                    int num = 0;

                    if (flagFrank != null && flagFrank == 0) {
                        if (item.getFlagRank() != null && item.getFlagRank() > 0) {
                            continue;
                        }
                    }

                    if (products != null && !products.isEmpty()) {
                        for (WeimobItem product : products) {
                            if (filterTxt != null && product.getGoodsTitle().indexOf(filterTxt) <= 0) {
                                continue;
                            }

                            if (orderType != null && milkIds != null) {
                                logger.info("ProductId = {}", product.getGoodsId());
                                if ((orderType == 0) != milkIds.contains(product.getGoodsId().longValue()))
                                    continue;

                            }

                            if (product.getRightsStatus() != null
                                    && (product.getRightsStatus() == 1 || product.getRightsStatus() == 2)) {

                                continue;
                            }
                            String goodsTitle = product.getGoodsTitle();
                            if (allProducts != null) {
                                MeeProductVo meeProduct = allProducts.get(product.getSkuCode());
                                if (meeProduct != null) {
                                    goodsTitle = meeProduct.getChName();
                                }
                            }
                            content.append(goodsTitle);
                            if (product.getSkuName() != null && !product.getSkuName().equals("")) {
                                content.append("【").append(product.getSkuName()).append("】");
                            }
                            content.append(" X ").append(product.getSkuNum()).append(";").append(product.getSkuCode())
                                    .append("<br>");
                            num += product.getSkuNum();
                        }
                    }

                    if (content == null || content.length() <= 0)
                        continue;

                    ListenableFuture<WeimobOrderDetailVo> task = GuavaExecutors.getDefaultCompletedExecutorService()
                            .submit(new Callable<WeimobOrderDetailVo>() {

                                @Override
                                public WeimobOrderDetailVo call() throws Exception {
                                    WeimobOrderDetailVo orderVo = getWeimobOrder("" + item.getOrderNo(), bizId);
                                    if (orderNo == null)
                                        logger.info("weimob is null orderId = {}", item.getOrderNo());
                                    return orderVo;
                                }

                            });

                    futures.add(task);

                    WeimobItemsResponse weimobItem = new WeimobItemsResponse();
                    weimobItem.setAddress(address);
                    weimobItem.setContent(content.toString());
                    weimobItem.setName(name);
                    weimobItem.setNum(num);
                    weimobItem.setOrderNo(orderNo);
                    weimobItem.setPhone(mobile);
                    weimobItem.setIdCardNo(null);
                    weimobItem.setSender(sender);

                    weimobItems.add(weimobItem);
                }
            }
        }

        ListenableFuture<List<WeimobOrderDetailVo>> resultsFuture = Futures.successfulAsList(futures);
        try {
            List<WeimobOrderDetailVo> orderDetails = resultsFuture.get();
            Map<Long, String> map = new HashMap<>();
            if (orderDetails != null && !orderDetails.isEmpty()) {
                for (WeimobOrderDetailVo goodDetailData : orderDetails) {
                    if (goodDetailData == null)
                        continue;
                    WeimobDeliveryDetailVo deliveryDetail = goodDetailData.getDeliveryDetail();
                    if (deliveryDetail != null) {
                        LogisticsDeliveryDetail logisticsDelivery = deliveryDetail.getLogisticsDeliveryDetail();
                        if (logisticsDelivery != null) {
                            String idCardNo = logisticsDelivery.getIdCardNo();
                            map.put(goodDetailData.getOrderNo(), idCardNo);

                            logger.info("{} IdcardNo = {}", goodDetailData.getOrderNo(), idCardNo);
                        } else
                            logger.info("logisticsDelivery is null");
                    } else {
                        logger.info("deliveryDetail is null!");
                    }
                }
            }

            if (map != null && !map.isEmpty()) {

                weimobItems.forEach((item) -> {
                    String idCardNo = map.get(item.getOrderNo());
                    if (!StringUtils.isEmpty(idCardNo))
                        item.setIdCardNo(idCardNo);
                });
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        response.setItems(weimobItems);

        return response;
    }


    private WeimobOrderListRespVo getListData(List<WeimobOrderData> datas, Integer sendArea, Integer orderType,
            Integer flagFrank, Long bizId) {
        if (datas == null || datas.isEmpty() || datas.size() <= 0)
            return null;

        String filterTxt = null;
        if (sendArea != null) {
            if (sendArea == 0) {
                filterTxt = "新西兰仓";
            } else if (sendArea == 1) {
                filterTxt = "国内现货";
            }
        }

        List<Long> milkIds = null;
        if (orderType != null) {
            milkIds = getMilkIds(bizId);
        }

        Map<String, MeeProductVo> allProducts = productsService.getMapMeeProduct(bizId.toString());

        WeimobOrderListRespVo response = new WeimobOrderListRespVo();
        List<ListenableFuture<WeimobOrderDetailVo>> futures = Lists.newArrayList();

        List<WeimobItemsRespVo> weimobItems = new ArrayList<>();

        for (int i = 0; i < datas.size(); i++) {
            WeimobOrderData data = datas.get(i);
            if (i == 0) {
                response.setPageNum(data.getPageNum());
                response.setPageSize(data.getPageSize());
                response.setTotalCount(data.getTotalCount());
            }
            List<WeimobOrderDataList> items = data.getPageList();
            if (items != null && items.size() > 0) {
                for (WeimobOrderDataList item : items) {
                    String address = item.getReceiverAddress();
                    String mobile = item.getReceiverMobile();
                    String name = item.getReceiverName();
                    Long orderNo = item.getOrderNo();

                    List<WeimobItem> products = item.getItemList();
                    List<WeimobItemVo> itemVos = Lists.newArrayList();
                    // StringBuffer content = new StringBuffer();
                    int num = 0;
                    if (flagFrank != null && flagFrank == 0) {
                        if (item.getFlagRank() != null && item.getFlagRank() > 0) {
                            continue;
                        }
                    }

                    if (products != null && !products.isEmpty()) {
                        for (WeimobItem product : products) {
                            if (filterTxt != null && product.getGoodsTitle().indexOf(filterTxt) <= 0) {
                                continue;
                            }

                            if (orderType != null && milkIds != null) {
                                logger.info("ProductId = {}", product.getGoodsId());
                                if ((orderType == 0) != milkIds.contains(product.getGoodsId().longValue()))
                                    continue;

                            }

                            if (product.getRightsStatus() != null
                                    && (product.getRightsStatus() == 1 || product.getRightsStatus() == 2)) {

                                continue;
                            }
                            String goodsTitle = product.getGoodsTitle();
                            if (allProducts != null) {
                                MeeProductVo meeProduct = allProducts.get(product.getSkuCode());
                                if (meeProduct != null) {
                                    goodsTitle = meeProduct.getChName();
                                }
                            }

                            WeimobItemVo iVo = new WeimobItemVo();
                            iVo.setImageUrl(product.getImageUrl());
                            iVo.setItemName(goodsTitle);
                            iVo.setNum(product.getSkuNum());
                            iVo.setSku(product.getSkuCode());
                            itemVos.add(iVo);

                            num += product.getSkuNum();
                        }
                    }

                    ListenableFuture<WeimobOrderDetailVo> task = GuavaExecutors.getDefaultCompletedExecutorService()
                            .submit(new Callable<WeimobOrderDetailVo>() {

                                @Override
                                public WeimobOrderDetailVo call() throws Exception {
                                    WeimobOrderDetailVo orderVo = getWeimobOrder("" + item.getOrderNo(), bizId);
                                    if (orderNo == null)
                                        logger.info("weimob is null orderId = {}", item.getOrderNo());
                                    return orderVo;
                                }

                            });

                    futures.add(task);

                    WeimobItemsRespVo weimobItem = new WeimobItemsRespVo();
                    weimobItem.setAddress(address);
                    weimobItem.setItems(itemVos);
                    weimobItem.setName(name);
                    weimobItem.setNum(num);
                    weimobItem.setOrderNo(orderNo);
                    weimobItem.setPhone(mobile);
                    weimobItem.setIdCardNo(null);

                    weimobItems.add(weimobItem);
                }
            }
        }

        ListenableFuture<List<WeimobOrderDetailVo>> resultsFuture = Futures.successfulAsList(futures);
        try {
            List<WeimobOrderDetailVo> orderDetails = resultsFuture.get();
            Map<Long, String> map = new HashMap<>();
            if (orderDetails != null && !orderDetails.isEmpty()) {
                for (WeimobOrderDetailVo goodDetailData : orderDetails) {
                    if (goodDetailData == null)
                        continue;
                    WeimobDeliveryDetailVo deliveryDetail = goodDetailData.getDeliveryDetail();
                    if (deliveryDetail != null) {
                        LogisticsDeliveryDetail logisticsDelivery = deliveryDetail.getLogisticsDeliveryDetail();
                        if (logisticsDelivery != null) {
                            String idCardNo = logisticsDelivery.getIdCardNo();
                            map.put(goodDetailData.getOrderNo(), idCardNo);

                            logger.info("{} IdcardNo = {}", goodDetailData.getOrderNo(), idCardNo);
                        } else
                            logger.info("logisticsDelivery is null");
                    } else {
                        logger.info("deliveryDetail is null!");
                    }
                }
            }

            if (map != null && !map.isEmpty()) {

                weimobItems.forEach((item) -> {
                    String idCardNo = map.get(item.getOrderNo());
                    if (!StringUtils.isEmpty(idCardNo))
                        item.setIdCardNo(idCardNo);
                });
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        response.setItems(weimobItems);

        return response;
    }

    private List<Long> getMilkIds(Long bizId) {
        GoodListQueryParameter queryParameter = new GoodListQueryParameter();
        queryParameter.setSearch(null);
        queryParameter.setGoodsClassifyId(657235243L);
        queryParameter.setGoodsStatus(0);

        List<Long> milkIds = null;
        List<GoodPageList> pageLists = getGoodList(queryParameter, bizId);
        if (pageLists != null && pageLists.size() > 0) {
            milkIds = new ArrayList<>();
            for (GoodPageList good : pageLists) {
                milkIds.add(good.getGoodsId());
            }
        }

        return milkIds;
    }

    private List<PriceUpdateResult> updatePrice(Long goodId, List<GoodPriceDetail> goodPrices, Long bizId) {
        if (goodPrices == null || goodPrices.isEmpty()) {
            return null;
        }

        List<PriceUpdateResult> results = new ArrayList<>();
        List<SkuList> skuList = new ArrayList<>();

        for (GoodPriceDetail priceDetail : goodPrices) {

            BigDecimal costPrice = priceDetail.getUpdateCostPrice() == null ? BigDecimal.ZERO
                    : priceDetail.getUpdateCostPrice();
            BigDecimal salePrice = priceDetail.getUpdateSalesPrice() == null ? BigDecimal.ZERO
                    : priceDetail.getUpdateSalesPrice();

            // String sku = priceDetail.getSku();
            // WeimobSkuVo weimobSku = skuVoMap.get(sku);
            BigDecimal oriPrice = priceDetail.getOriginalPrice();
            Long skuId = priceDetail.getSkuId();

            SkuList skuVo = new SkuList();
            skuVo.setSkuId(skuId);
            skuVo.setCostPrice(costPrice);
            skuVo.setOriginalPrice(oriPrice);
            skuVo.setSalePrice(salePrice);
            skuList.add(skuVo);

        }

        logger.info("skuList = {}", skuList);
        // UpdatePrice
        if (skuList != null && !skuList.isEmpty()) {
            WeimobUpdateParams params = new WeimobUpdateParams();
            params.setGoodsId(goodId);
            params.setOperateType(2);
            params.setSkuList(skuList);

            logger.info("params = {}", params);
            boolean isSucc = updateWeimobGood(params, bizId);
            for (SkuList sku : skuList) {
                PriceUpdateResult result = new PriceUpdateResult();
                result.setSku("" + sku.getSkuId());
                result.setSuccess(isSucc);
                results.add(result);
            }
        }

        return results;
    }

    private boolean updateWeimobGood(WeimobUpdateParams params, Long bizId) {
        if (params == null)
            return false;

        String token = getToken(bizId);
        String url = weimobConfig.getUpdateGoodUrl() + "?accesstoken=" + token;

        String result = JoddHttpUtils.sendPostUseBody(url, params);
        if (result == null || result.isEmpty())
            return false;

        logger.info(result);
        UpdateGoodResponse response = JSON.parseObject(result, UpdateGoodResponse.class);
        if (response == null || response.getCode() == null || !response.getCode().getErrcode().equals("0"))
            return false;

        return response.getData().isResult();
    }

    private List<String> getErrorOrderId(List<DeliveryOrderVo> deleverOrders) {
        if (deleverOrders == null || deleverOrders.isEmpty())
            return null;

        List<String> orderIds = new ArrayList<>();
        for (DeliveryOrderVo deliveryOrder : deleverOrders) {
            orderIds.add(deliveryOrder.getOrderId());
        }

        return orderIds;
    }

    @Override
    public boolean flagOrders(Long bizId, Integer flagRank, List<String> orderIds) {
        String token = getToken(bizId);
        String url = weimobConfig.getFlagOrderUrl() + "?accesstoken=" + token;

        Map<String, WeimobFlagOrderParam> flagParams = new HashMap<>();
        orderIds.stream().forEach((item) -> {
            
            String[] str = item.split("-")[0].split(";");
            for (String s : str) {
                if (StringUtils.isEmpty(s))
                    continue;

                String key = s;

                WeimobFlagOrderParam p = flagParams.get(key);
                if(p == null) {
                    p = new WeimobFlagOrderParam();
                    p.setFlagRank(flagRank);
                    p.setFlagContent("易云系统已处理<br>" + "订单编号:" + item + "/");
                    p.setOrderNoList(Lists.newArrayList(Long.parseLong(key)));
                } else {
                    String content = p.getFlagContent();
                    if(!StringUtils.isEmpty(content)) {
                        String[] contents = content.split("<br>");
                        for(String c: contents) {
                            if(c.indexOf("订单编号:") >= 0) {
                                c += item + "/";
                            }
                        };
                        p.setFlagContent(Joiner.on("<br>").join(contents));
                    }
                }
                flagParams.put(key, p);
            }
        });

        List<ListenableFuture<Boolean>> futures = Lists.newArrayList();
        for(WeimobFlagOrderParam param: flagParams.values()) {
            ListenableFuture<Boolean> task = GuavaExecutors.getDefaultCompletedExecutorService()
            .submit(new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    boolean flag = false;
                    try {
                       flag = flagOrder(url,param);
                    } catch (Exception e) {
                        logger.error("flagOrder Error {} ", param ,e);
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
            logger.error("flagOrder Total Error", e);

        } catch (ExecutionException e) {
            e.printStackTrace();
            logger.error("flagOrder Total Exp", e);

        }
        
        return result;
    }

    private boolean flagOrder(String url, WeimobFlagOrderParam param) {
        String result = JoddHttpUtils.sendPostUseBody(url, param);
        if (result == null || result.isEmpty())
            return false;

        logger.info(result);
        WeimobFlagResponse response = JSON.parseObject(result, WeimobFlagResponse.class);
        if (response == null || response.getCode() == null || response.getData() == null
                || !response.getCode().getErrcode().equals("0") || !response.getData().getSuccess())
            return false;

        return true;
    }

    @Override
    public boolean flagLoadOrders(Long bizId, List<String> orderIds) {
        return this.flagOrders(bizId, 1, orderIds);
    }

    @Override
    public List<WeimobOrderDataList> getDeliveryOrder(Long bizId, WeimobDeliveryVo request) throws MeeException {
        
        WeimobOrderListRequest param = new WeimobOrderListRequest();
        param.setCreateEndTime(request.getCreateEndTime());
        param.setCreateStartTime(request.getCreateStartTime());

        param.setFlagRanks(1);
        param.setOrderStatuses(WeimobOrderStatusEnum.PAID.getCode());
        param.setPageNum(1);
        param.setPageSize(100);

        List<WeimobOrderData> orderData = getOrderData(param, bizId);
        if(orderData == null || orderData.size() <= 0) {
            return null;
        }

        List<WeimobOrderDataList> datas = Lists.newArrayList();

        for(WeimobOrderData data: orderData) {
            List<WeimobOrderDataList> pageList = data.getPageList();
            if(pageList == null || pageList.isEmpty())
                continue;
            
            datas.addAll(pageList);
        }

        List<WeimobOrderDataList> result = null; 
        if( datas != null) {
            result =  Lists.newArrayList();

            Set<String> extId = new HashSet<>();
            datas.stream().forEach(item -> {
                String[] str = null;
                if(!StringUtils.isEmpty(item.getFlagContent())) {
                    str = getOrderIdByContent(item.getFlagContent());

                }  
                if (str == null || str.length <= 0) {
                    extId.add(item.getOrderNo().toString());
                } else {
                    for(String s: str) {
                        if(!StringUtils.isEmpty(s))
                            extId.add(s);
                    }
                }
            });

            // List<String> extId = datas.stream().map(item -> item.getOrderNo().toString()).collect(Collectors.toList());
            List<YiyunOrderSales> orders = orderService.getYiyunOrderByExtId(bizId, extId);
            if(orders == null) {
                return null;
            }

            for(WeimobOrderDataList item: datas) {
                List<YiyunOrderSales> logicIds = orders.stream().
                                            filter(i -> i.getExternalId().replace("\"", "").indexOf(item.getOrderNo().toString()) >= 0 && i.getLogistic() != null &&  i.getLogistic().getLogisticId() != null).
                                            collect(Collectors.toList());
                
                if(logicIds != null && logicIds.size() > 0) {
                    for(YiyunOrderSales orderSales: logicIds) {
                        item.setDeliveryOrderId(Long.parseLong(orderSales.getLogistic().getLogisticId()));

                        item.setDeliveryCom(orderSales.getLogistic().getLogisticCom());
                        item.setDeliveryCode(WeimobDeliveryCompany.getExpCodeByName(orderSales.getLogistic().getLogisticCom()));

                        List<WeimobItem> items = item.getItemList().stream().
                            filter(i -> 
                                orderSales.getorderDetail().stream().
                                    filter(l -> l.getSku().equals(i.getSkuCode())).
                                    count() > 0 ).
                            collect(Collectors.toList());
                        logger.info("WeimobItem {}", items);   
                        item.setSplit(items.size() < item.getItemList().size()); 
                        item.setItemList(items);
                        result.add(item);
                    }
                }
            }
        }

        return result;
    }

    private String[] getOrderIdByContent(String content) {
        if(StringUtils.isEmpty(content)){
            return null;
        }

        if(content.indexOf("订单编号") < 0) {
            return null;
        }

        String[] contents = content.split("<br>");
        if(contents == null || contents.length <= 0) {
            return null;
        }

        
        String[] str = null;
        for (String c: contents) {
            if(c.indexOf("订单编号:") >= 0 && c.length() > 5) {
                str = c.substring(5).split("/");
            }
        }

        return str;
    }


}

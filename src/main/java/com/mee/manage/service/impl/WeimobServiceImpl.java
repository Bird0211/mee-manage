package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;
import com.mee.manage.po.Configuration;
import com.mee.manage.service.IConfigurationService;
import com.mee.manage.service.IOCRService;
import com.mee.manage.service.IProductsService;
import com.mee.manage.service.IWeimobService;
import com.mee.manage.util.*;
import com.mee.manage.vo.*;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

@Service
public class WeimobServiceImpl implements IWeimobService {

    private static final Logger logger = LoggerFactory.getLogger(IWeimobService.class);

    @Autowired
    IConfigurationService configurationService;

    @Autowired
    WeimobConfig weimobConfig;

    @Autowired
    IProductsService productsService;


    @Override
    public boolean addCode(String code) {
        if(code == null)
            return false;

        Map<String,Object> data = new HashMap<>();
        data.put("code",code);
        data.put("grant_type","authorization_code");
        data.put("client_id",weimobConfig.getClientId());
        data.put("client_secret",weimobConfig.getClientSecret());
        data.put("redirect_uri",weimobConfig.getReturnUri());


        logger.info(JSON.toJSONString(data));
        String result = JoddHttpUtils.sendPost(weimobConfig.getWeimobTokenUrl(),data);
        logger.info(result);
        WeimobTokenResponse weimobTokenResponse = JSON.parseObject(result,WeimobTokenResponse.class);
        return saveToken(weimobTokenResponse);
    }

    @Override
    public CheckTokenResult checkToken() {

        boolean flag = false;
        String token = null;
        Configuration tokenConfig = configurationService.getConfig(Config.WEIMOBTOKEN);
        if(tokenConfig != null) {
            if (tokenConfig.getExpir().after(new Date())) {   //token > new date()
                flag = true;
                token = tokenConfig.getValue();
            } else {
                Configuration refreshToken = configurationService.getConfig(Config.WEIMOBREREFRESHTOKEN);
                if (refreshToken != null &&
                        refreshToken.getExpir().after(new Date())) { //refreshToken < new date()
                    return refreshToken(refreshToken.getValue());
                }
            }
        }

        CheckTokenResult tokenResult = new CheckTokenResult();
        tokenResult.setCuccess(flag);
        tokenResult.setToken(token);
        return tokenResult;
    }

    @Override
    public String getToken(){
        CheckTokenResult tokenResult = checkToken();
        if (tokenResult == null || !tokenResult.isCuccess()) {
            return null;
        }

        String token = tokenResult.getToken();
        logger.info("Token = {}",token);
        return token;
    }

    @Override
    public CheckTokenResult refreshToken(String refreshToken) {
        CheckTokenResult tokenResult = new CheckTokenResult();
        if(refreshToken == null) {
            tokenResult.setCuccess(false);
            return tokenResult;
        }

        Map<String,Object> data = new HashMap<>();
        data.put("grant_type","refresh_token");
        data.put("client_id", weimobConfig.getClientId());
        data.put("client_secret",weimobConfig.getClientSecret());
        data.put("refresh_token",refreshToken);
        data.put("redirect_uri",weimobConfig.getReturnUri());

        String result = JoddHttpUtils.sendPost(weimobConfig.getWeimobTokenUrl(),data);
        WeimobTokenResponse weimobTokenResponse = JSON.parseObject(result,WeimobTokenResponse.class);

        if(weimobTokenResponse == null) {
            tokenResult.setCuccess(false);
            return tokenResult;
        }
        boolean isSaveToken = saveToken(weimobTokenResponse);
        tokenResult.setCuccess(isSaveToken);
        if(isSaveToken) {
            tokenResult.setToken(weimobTokenResponse.getAccess_token());
        }
        return tokenResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setToken(String token, Date expire, String refreshToken, Date expireRefreshToken) {
        if(token == null || refreshToken == null ||
                expire.before(new Date()) || expireRefreshToken.before(new Date()))
            return false;

        boolean flag = false;
        logger.info("saveToken Token: {}; RefreshToken: {}",token,refreshToken);

        //入库、事务
        try {
            Configuration tokenConfig = configurationService.getConfig(Config.WEIMOBTOKEN);
            if(tokenConfig == null)
                configurationService.insertConfig(Config.WEIMOBTOKEN,token,expire);
            else
                configurationService.updateConfig(Config.WEIMOBTOKEN,token,expire);

            Configuration reFreshTokenConfig = configurationService.getConfig(Config.WEIMOBREREFRESHTOKEN);
            if(reFreshTokenConfig == null)
                configurationService.insertConfig(Config.WEIMOBREREFRESHTOKEN,refreshToken,expireRefreshToken);
            else
                configurationService.updateConfig(Config.WEIMOBREREFRESHTOKEN,refreshToken,expireRefreshToken);

            flag = true;
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("withDraw Err " ,ex);
            flag = false;
        }

        return flag;
    }

    @Override
    public MeeResult getOrderList(WeimobOrderListRequest request) {
        MeeResult meeResult = new MeeResult();
        if(request == null) {
            meeResult.setStatusCode(StatusCode.PARAM_ERROR.getCode());
            return meeResult;
        }

        CheckTokenResult tokenResult = checkToken();
        if (tokenResult == null || !tokenResult.isCuccess()) {
            meeResult.setStatusCode(StatusCode.PARAM_ERROR.getCode());
            return meeResult;
        }

        String token = tokenResult.getToken();
        logger.info("Token = {}",token);
        String url = weimobConfig.getWeimobOrderListUrl()+"?accesstoken="+token;

        WeimobOrderRequest orderRequest = new WeimobOrderRequest();
        int pageNum = 1;
        int pageSize = request.getPageSize();
        int totalCount = 0;
        StatusCode statusCode = null;

        List<WeimobOrderData> datas = new ArrayList<>();

        do {
            //代码语句
            orderRequest.setPageNum(pageNum);
            orderRequest.setPageSize(pageSize);

            WeimobQueryParameter parameter = new WeimobQueryParameter();
            parameter.setCreateStartTime(request.getCreateStartTime().getTime());
            parameter.setCreateEndTime(request.getCreateEndTime().getTime());
            if(request.getOrderStatuses() != null)
                parameter.setOrderStatuses(new Integer[]{request.getOrderStatuses()});
            if(request.getSendarea() != null) {
                if (request.getSendarea() == 0)
                    parameter.setKeyword("新西兰仓");
                else if (request.getSendarea() == 1) {
                    parameter.setKeyword("国内现货");
                }
                parameter.setSearchType(1);//搜索类型 (1商品名称，2商品编码，3客户昵称，4订单编号，5收货人姓名，6收货人手机号，7交易单号，8商户单号，9提货码，99多字段搜索)
            }
            orderRequest.setQueryParameter(parameter);
            String result = JoddHttpUtils.sendPostUseBody(url,orderRequest);
            if(result == null) {
                statusCode = StatusCode.SYS_ERROR;
                break;
            }

            if(result.indexOf("80001001000119") >=0 ){
                statusCode = StatusCode.WEIMOB_TOKEN_ERROR;
                configurationService.removeConfig(Config.WEIMOBTOKEN);
                configurationService.removeConfig(Config.WEIMOBREREFRESHTOKEN);

                break;
            }

            logger.info(result);
            WeimobOrderResponse orderResponse = JSON.parseObject(result,WeimobOrderResponse.class, Feature.IgnoreNotMatch);
            if(orderResponse == null){
                statusCode = StatusCode.SYS_ERROR;
                break;
            }

            WeimobOrderCode code = orderResponse.getCode();
            if(code == null){
                statusCode = StatusCode.SYS_ERROR;
                break;
            }

            if(code.getErrcode().equals("80001001000119")){
                statusCode = StatusCode.WEIMOB_TOKEN_ERROR;
                break;
            }

            if(code.getErrcode().equals("0")){
                WeimobOrderData data = orderResponse.getData();
                totalCount = data.getTotalCount();
                datas.add(data);
                statusCode = StatusCode.SUCCESS;
            }else{
                statusCode = StatusCode.WEIMOB_TOKEN_ERROR;
                break;
            }
            pageNum++;
        }while((pageNum-1) * pageSize < totalCount);

        if(statusCode == StatusCode.SUCCESS) {
            meeResult.setData(getOrderListData(datas,request.getSendarea(),request.getOrderType()));
        }
        meeResult.setStatusCode(statusCode.getCode());
        return meeResult;
    }

    @Override
    public List<WeimobGroupVo> getClassifyInfo() {
        String token = getToken();
        if(token == null)
            return null;

        String url = weimobConfig.getGoodsClassifyUrl()+"?accesstoken="+token;
        String result = JoddHttpUtils.sendPost(url,null);
        if(result == null || result.isEmpty())
            return null;

        logger.info(result);
        WeimobGoodsClassifyResponse goodsClassifyResponse =
                JSON.parseObject(result,WeimobGoodsClassifyResponse.class);

        if(goodsClassifyResponse == null)
            return null;

        WeimobOrderCode code = goodsClassifyResponse.getCode();
        if(code == null || !code.getErrcode().equals("0"))
            return null;

        WeimobGoodsClassifyData data = goodsClassifyResponse.getData();
        if(data == null)
            return null;

        List<GoodsClassify> goodsClassifies = data.getGoodsClassifyList();
        if(goodsClassifies == null || goodsClassifies.isEmpty())
            return null;

        return getGroupList(goodsClassifies);
    }

    @Override
    public WeimobOrderDetailVo getWeimobOrder(String orderId) {
        if(orderId == null)
            return null;

        String token = getToken();
        if(token == null)
            return null;

        Map<String,Object> params = new HashMap<>();
        params.put("orderNo",orderId);
        params.put("needInvoiceInfo",false);
        params.put("needMemberInfo",false);

        String url = weimobConfig.getOrderDetail()+"?accesstoken="+token;
        logger.info(JSON.toJSONString(params));
        String result = JoddHttpUtils.sendPostUseBody(url,params);
        if(result == null || result.isEmpty())
            return null;

        logger.info(result);

        WeimobOrderDetailVo orderDetail = null;
        WeimobOrderVo weimobOrder = JSON.parseObject(result,WeimobOrderVo.class,Feature.IgnoreNotMatch);
        if(weimobOrder != null && weimobOrder.getCode().getErrcode().equals("0")) {
            orderDetail = weimobOrder.getData();
        }
        return orderDetail;
    }

    @Override
    public List<GoodPageList> getGoodList(GoodListQueryParameter params) {

        String token = getToken();
        if(StringUtils.isEmpty(token))
            return null;

        GoodListRequest goodListRequest = new GoodListRequest();
        goodListRequest.setQueryParameter(params);
        String url = weimobConfig.getGoodListUrl()+"?accesstoken="+token;
        int pageNum = 1;
        int pageSize = 20;
        int totalCount = 0;
        List<GoodPageList> pageList = new ArrayList<>();

        do {
            goodListRequest.setPageNum(pageNum);
            goodListRequest.setPageSize(pageSize);

            String result = JoddHttpUtils.sendPostUseBody(url,goodListRequest);
            if (result == null || result.isEmpty())
                break;

            logger.info("QueryGoodList = {}",result);
            GoodListResponse goodListResponse = JSON.parseObject(result,GoodListResponse.class);
            if(goodListResponse == null)
                break;

            WeimobOrderCode code = goodListResponse.getCode();
            if(code == null)
                break;

            if(code.getErrcode() == null || !code.getErrcode().equals("0"))
                break;

            GoodListData data = goodListResponse.getData();
            totalCount = data.getTotalCount();

            pageList.addAll(data.getPageList());

            pageNum ++;

        }while ((pageNum-1) * pageSize < totalCount);


        return pageList;
    }

    @Override
    public List<GoodInfoVo> getWeimobGoods(GoodListQueryParameter params) {

        List<GoodPageList> goodList = getGoodList(params);
        Map<String,MeeProductVo> allProducts = productsService.getMapMeeProduct();

        List<GoodInfoVo>  goodInfos = new ArrayList<>();

        if(goodList != null && !goodList.isEmpty()) {
            List<ListenableFuture<GoodDetailData>> futures = Lists.newArrayList();
            for (GoodPageList good : goodList) {
                ListenableFuture<GoodDetailData> task = JayGuavaExecutors.getDefaultCompletedExecutorService()
                        .submit(new Callable<GoodDetailData>() {

                            @Override
                            public GoodDetailData call() throws Exception {
                                GoodDetailData goodData = getWeimobGoodDetail(good.getGoodsId());
                                return goodData;
                            }

                        });

                futures.add(task);
            }

            ListenableFuture<List<GoodDetailData>> resultsFuture = Futures.successfulAsList(futures);
            try {
                List<GoodDetailData> datas = resultsFuture.get();
                if(datas != null && !datas.isEmpty()) {
                    for (GoodDetailData goodDetailData : datas) {
                        List<GoodInfoVo> finalGoodInfos = stGoodInfo(goodDetailData,allProducts);
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

    private List<GoodInfoVo> stGoodInfo(GoodDetailData goodData,Map<String,MeeProductVo> allProducts ) {
        List<GoodInfoVo> goodInfos = null;
        if(goodData != null) {

            GoodDetailVo goodDetail = goodData.getGoods();
            if(goodDetail == null)
                return null;

            goodInfos = new ArrayList<>();
            List<WeimobSkuVo> skuList = goodDetail.getSkuList();
            if(skuList != null && !skuList.isEmpty()) {
                for (WeimobSkuVo skuVo : skuList) {
                    GoodInfoVo goodInfo = new GoodInfoVo();

                    Map<String,SkuAttrMap> skuAttrMap = skuVo.getSkuAttrMap();
                    StringBuffer sb = new StringBuffer(skuVo.getProductTitle());
                    if(skuAttrMap != null){
                        Set<String> keys = skuAttrMap.keySet();
                        if(keys != null && !keys.isEmpty()) {
                            for(String key : keys) {
                                SkuAttrMap attrMap = skuAttrMap.get(key);
                                sb.append("[").append(attrMap.getName()).append(":").append(attrMap.getValue()).append("]");
                            }
                        }
                    }

                    if(sb != null && sb.length() > 0) {
                        goodInfo.setTitle(sb.toString());

                    }

                    if(!StringUtils.isEmpty(skuVo.getImageUrl())) {
                        goodInfo.setDefaultImageUrl(skuVo.getImageUrl());

                    }

                    if(skuVo.getGoodsId() != null && skuVo.getGoodsId() != 0) {
                        goodInfo.setGoodsId(skuVo.getGoodsId());

                    }

                    goodInfo.setSalesPrice(skuVo.getSalePrice());
                    goodInfo.setCostPrice(skuVo.getCostPrice());

                    String outerSky = skuVo.getOuterSkuCode();
                    if(!StringUtils.isEmpty(outerSky))
                        goodInfo.setSku(skuVo.getOuterSkuCode().split("_")[0]);

                    if(StringUtils.isEmpty(goodInfo.getDefaultImageUrl())) {
                        List<String> images = goodDetail.getGoodsImageUrl();
                        if(images != null && images.size() > 0) {
                            goodInfo.setDefaultImageUrl(images.get(0));
                        }
                    }

                    MeeProductVo meeProduct = allProducts.get(goodInfo.getSku());
                    if(meeProduct != null) {
                        goodInfo.setYiyunCostPrice(meeProduct.getCostPrice());
                        goodInfo.setYiyunSalesPrice(meeProduct.getRetailPrice());
                        goodInfo.setWeight(meeProduct.getWeight());
                    }
                    goodInfos.add(goodInfo);
                }
            }

        }

        return goodInfos;
    }

    @Override
    public GoodDetailData getWeimobGoodDetail(Long goodId) {
        if(goodId == null)
            return null;

        String token = getToken();
        String url = weimobConfig.getGoodDetailUrl()+"?accesstoken="+token;
        Map<String,Object> params = new HashMap<>();
        params.put("goodsId",goodId);
        String result = JoddHttpUtils.sendPostUseBody(url,params);
        if(result == null || result.isEmpty())
            return null;

        logger.info(result);
        GoodDetailResponse detailResponse = JSON.parseObject(result,GoodDetailResponse.class);
        if(detailResponse == null)
            return null;

        WeimobOrderCode code = detailResponse.getCode();
        if(code == null)
            return null;

        if(code.getErrcode() == null || !code.getErrcode().equals("0"))
            return null;

        GoodDetailData goodDetail = detailResponse.getData();
        return goodDetail;
    }

    private List<WeimobGroupVo> getGroupList(List<GoodsClassify> goodsClassifies){
        if(goodsClassifies == null || goodsClassifies.isEmpty())
            return null;

        List<WeimobGroupVo> groupVos = new ArrayList<>();
        for (GoodsClassify goodsClassify : goodsClassifies){
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
            if(childrenGroup != null && childrenGroup.size() > 0)
                weimobGroupVo.setChildrenGroup(childrenGroup);

            groupVos.add(weimobGroupVo);
        }

        return groupVos;
    }


    private boolean saveToken(WeimobTokenResponse refreshTokenResponse){
        if(refreshTokenResponse == null)
            return false;

        String token = refreshTokenResponse.getAccess_token();
        String reToken = refreshTokenResponse.getRefresh_token();
        int expireToken = refreshTokenResponse.getExpires_in();
        int expireRefreshToken = refreshTokenResponse.getRefresh_token_expires_in();

        return setToken(token,DateUtil.getSuffixSecond(expireToken),reToken, DateUtil.getSuffixSecond(expireRefreshToken));
    }

    private WeimobOrderListResponse getOrderListData(List<WeimobOrderData> datas,Integer sendArea,Integer orderType){
        if(datas == null || datas.isEmpty() || datas.size() <=0)
            return null;

        String filterTxt = null;
        if(sendArea != null) {
            if(sendArea == 0) {
                filterTxt = "新西兰仓";
            }else if(sendArea == 1) {
                filterTxt = "国内现货";
            }
        }

        List<Long> milkIds = null;
        if(orderType != null) {
            milkIds = getMilkIds();
        }

        Map<String,MeeProductVo> allProducts = productsService.getMapMeeProduct();

        WeimobOrderListResponse response = new WeimobOrderListResponse();
        List<WeimobItemsResponse> weimobItems = new ArrayList<>();
        for(int i = 0; i < datas.size();i++) {
            WeimobOrderData data = datas.get(i);
            if(i == 0) {
                response.setPageNum(data.getPageNum());
                response.setPageSize(data.getPageSize());
                response.setTotalCount(data.getTotalCount());
            }
            List<WeimobOrderDataList> items = data.getPageList();
            if(items != null && items.size() > 0) {
                for (WeimobOrderDataList item : items) {
                    String address = item.getReceiverAddress();
                    String mobile = item.getReceiverMobile();
                    String name = item.getReceiverName();
                    Long orderNo = item.getOrderNo();

                    List<WeimobItem> products = item.getItemList();
                    StringBuffer content = new StringBuffer();
                    int num = 0;

                    if(products != null && !products.isEmpty()) {
                        for (WeimobItem product : products) {
                            if(filterTxt != null && product.getGoodsTitle().indexOf(filterTxt) <= 0) {
                                continue;
                            }

                            if(orderType != null && milkIds != null) {
                                logger.info("ProductId = {}",product.getGoodsId());
                                if ((orderType == 0) != milkIds.contains(product.getGoodsId().longValue()))
                                    continue;

                            }
                            String goodsTitle = product.getGoodsTitle();
                            if(allProducts != null) {
                                MeeProductVo meeProduct = allProducts.get(product.getSkuCode());
                                if(meeProduct != null) {
                                    goodsTitle = meeProduct.getChName();
                                }
                            }
                            content.append(goodsTitle);
                            if(product.getSkuName() != null && !product.getSkuName().equals("")) {
                                content.append("【").append(product.getSkuName()).append("】");
                            }
                            content.append(" X ").append(product.getSkuNum()).append(";").append(product.getSkuCode()).append("<br>");
                            num += product.getSkuNum();
                        }
                    }

                    if(content == null || content.length() <= 0)
                        continue;

                    String idCardNo = null;
                    WeimobOrderDetailVo orderVo = getWeimobOrder(""+item.getOrderNo());
                    if(orderVo != null ) {
                        WeimobDeliveryDetailVo deliveryDetail = orderVo.getDeliveryDetail();
                        if(deliveryDetail != null) {
                            LogisticsDeliveryDetail logisticsDelivery = deliveryDetail.getLogisticsDeliveryDetail();
                            if(logisticsDelivery != null) {
                                idCardNo = logisticsDelivery.getIdCardNo();
                                logger.info("{} IdcardNo = {}", item.getOrderNo(), idCardNo);
                            }else
                                logger.info("logisticsDelivery is null");
                        }else {
                            logger.info("deliveryDetail is null!");
                        }
                    } else
                        logger.info("OrderDetailVo is NULL");

                    WeimobItemsResponse weimobItem = new WeimobItemsResponse();
                    weimobItem.setAddress(address);
                    weimobItem.setContent(content.toString());
                    weimobItem.setName(name);
                    weimobItem.setNum(num);
                    weimobItem.setOrderNo(orderNo);
                    weimobItem.setPhone(mobile);
                    weimobItem.setIdCardNo(idCardNo);

                    weimobItems.add(weimobItem);
                }
            }
        }
        response.setItems(weimobItems);

        return response;
    }

    private List<Long> getMilkIds(){

        GoodListQueryParameter queryParameter = new GoodListQueryParameter();
        queryParameter.setSearch(null);
        queryParameter.setGoodsClassifyId(657235243L);
        queryParameter.setGoodsStatus(0);

        List<Long> milkIds = null;
        List<GoodPageList> pageLists = getGoodList(queryParameter);
        if(pageLists != null && pageLists.size() > 0) {
            milkIds = new ArrayList<>();
            for (GoodPageList good : pageLists) {
                milkIds.add(good.getGoodsId());
            }
        }

        return milkIds;
    }



}

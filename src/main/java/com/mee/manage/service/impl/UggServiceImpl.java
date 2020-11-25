package com.mee.manage.service.impl;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mee.manage.config.UggConfig;
import com.mee.manage.enums.PlatFormCodeEmn;
import com.mee.manage.enums.UggOrderStatusEmun;
import com.mee.manage.exception.MeeException;
import com.mee.manage.po.PlatformConfig;
import com.mee.manage.po.UggOrder;
import com.mee.manage.service.IDelivery;
import com.mee.manage.service.IDeliveryContext;
import com.mee.manage.service.IPlatformConfigService;
import com.mee.manage.service.IUggOrderService;
import com.mee.manage.service.IUggService;
import com.mee.manage.util.GuavaExecutors;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.Tools;
import com.mee.manage.vo.ugg.DFOrder;
import com.mee.manage.vo.ugg.DFOrderResp;
import com.mee.manage.vo.ugg.OrderCountResult;
import com.mee.manage.vo.ugg.OrderListResult;
import com.mee.manage.vo.ugg.QueryOrder;
import com.mee.manage.vo.ugg.QueryOrderRsp;
import com.mee.manage.vo.ugg.QueryParams;
import com.mee.manage.vo.ugg.UggLogin;
import com.mee.manage.vo.ugg.UggLoginResult;
import com.mee.manage.vo.ugg.UggLoginRsp;
import com.mee.manage.vo.ugg.UggOrderData;
import com.mee.manage.vo.ugg.UggProductDetail;
import com.mee.manage.vo.ugg.UggResult;
import com.mee.manage.vo.ugg.UggToken;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UggServiceImpl implements IUggService {

    private static final Logger logger = LoggerFactory.getLogger(IUggService.class);

    @Autowired
    IPlatformConfigService platformService;

    @Autowired
    UggConfig config;

    @Autowired
    IUggOrderService uggOrderService;

    @Autowired
    IDeliveryContext deliveryContext;

    @Override
    public String authToken(Long bizId, String username, String password) throws MeeException {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }

        String url = config.getTokenUrl();
        Map<String, String> params = new HashMap<>();
        params.put("user", username);
        params.put("password", password);

        String result = JoddHttpUtils.getData(url, params);
        if (StringUtils.isEmpty(result)) {
            throw new MeeException(StatusCode.FAIL);
        }

        UggResult<UggToken> response = JSON.parseObject(result, new TypeReference<UggResult<UggToken>>() {
        });
        if (response == null || response.getCode() != 200) {
            throw new MeeException(StatusCode.FAIL);
        }

        UggToken uggToken = response.getResult();

        PlatformConfig pConfig = new PlatformConfig();
        pConfig.setBizId(bizId);
        pConfig.setName(PlatFormCodeEmn.UGG.name());
        pConfig.setToken(uggToken.getToken());
        pConfig.setPlatformCode(PlatFormCodeEmn.UGG.getCode());
        pConfig.setClientId(username);
        pConfig.setClientSecret(password);
        pConfig.setExpire(new Date(uggToken.getExp() * 1000));

        boolean flag = platformService.updatePlatFormCode(pConfig);
        if (!flag)
            throw new MeeException(StatusCode.FAIL);

        return uggToken.getToken();
    }

    @Override
    public String login(Long bizId, String username, String password) throws MeeException {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }

        String url = config.getLoginUrl();
        UggLogin loginVo = new UggLogin();
        loginVo.setAccount(username);
        loginVo.setPassword(password);

        String result = JoddHttpUtils.sendPostUseBody(url, loginVo);
        if (StringUtils.isEmpty(result)) {
            throw new MeeException(StatusCode.FAIL);
        }

        UggLoginResult<UggLoginRsp> response = JSON.parseObject(result,
                new TypeReference<UggLoginResult<UggLoginRsp>>() {
                });
        if (response == null || response.getStatus() != 0) {
            throw new MeeException(StatusCode.FAIL);
        }

        UggLoginRsp uggToken = response.getData();

        PlatformConfig pConfig = new PlatformConfig();
        pConfig.setBizId(bizId);
        pConfig.setName(PlatFormCodeEmn.UGG_LOGIN.name());
        pConfig.setToken(uggToken.getToken());
        pConfig.setPlatformCode(PlatFormCodeEmn.UGG_LOGIN.getCode());
        pConfig.setClientId(username);
        pConfig.setClientSecret(password);

        boolean flag = platformService.updatePlatFormCode(pConfig);
        if (!flag)
            throw new MeeException(StatusCode.FAIL);

        return uggToken.getToken();
    }

    public String refreshToken(Long bizId) {
        PlatformConfig pConfig = platformService.getOnePlatForm(bizId, PlatFormCodeEmn.UGG_LOGIN.getCode());
        if(pConfig == null) {
            return null;
        }

        return login(bizId, pConfig.getClientId(), pConfig.getClientSecret());
    }

    @Override
    public String getToken(Long bizId) throws MeeException {
        PlatformConfig platForm = platformService.getOnePlatForm(bizId, PlatFormCodeEmn.UGG.getCode());
        if (platForm == null) {
            throw new MeeException(StatusCode.AUTH_FAIL);
        }

        String token = null;
        if (platForm.getExpire().before(new Date())) {
            token = authToken(bizId, platForm.getClientId(), platForm.getClientSecret());
        } else {
            token = platForm.getToken();
        }

        return token;
    }

    @Override
    public String getLoginToken(Long bizId) throws MeeException {
        PlatformConfig platForm = platformService.getOnePlatForm(bizId, PlatFormCodeEmn.UGG_LOGIN.getCode());
        if (platForm == null) {
            throw new MeeException(StatusCode.AUTH_FAIL);
        }

        String token = platForm.getToken();
        return token;
    }

    @Override
    public UggProductDetail getDetailBySKU(Long bizId, Long sku) throws MeeException {
        String token = getToken(bizId);

        String url = config.getDetailBySKUUrl();
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("barcode", sku.toString());

        String result = JoddHttpUtils.getData(url, params);
        if (StringUtils.isEmpty(result)) {
            throw new MeeException(StatusCode.FAIL);
        }

        UggResult<UggProductDetail> response = JSON.parseObject(result,
                new TypeReference<UggResult<UggProductDetail>>() {
                });
        if (response == null || response.getCode() != 200) {
            throw new MeeException(StatusCode.FAIL);
        }

        return response.getResult();
    }

    @Override
    public boolean saveUggOrder(UggOrder order, Long bizId) throws MeeException {
        if (order == null) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }

        order.setCreateTime(new Date());
        order.setStatus(UggOrderStatusEmun.Pretreatment.getCode());

        return uggOrderService.save(order);
    }

    @Override
    public String createBatchOrder(List<UggOrder> orders) throws MeeException {
        if (orders == null || orders.isEmpty()) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }

        Integer bizId = orders.get(0).getBizId();
        String batId = Tools.getBatchId(bizId);
        orders.stream().forEach(item -> {
            item.setBatchId(batId);
            item.setStatus(UggOrderStatusEmun.PrePay.getCode());
        });

        boolean flag = uggOrderService.updateBatchById(orders);
        if (!flag)
            throw new MeeException(StatusCode.DB_ERROR);

        return batId;
    }

    @Override
    public boolean sendOrders(List<UggOrder> orders, Long bizId) throws MeeException {
        if (orders == null || orders.isEmpty()) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }

        List<ListenableFuture<Boolean>> futures = Lists.newArrayList();
        for (UggOrder order : orders) {
            ListenableFuture<Boolean> task = GuavaExecutors.getDefaultCompletedExecutorService()
                    .submit(new Callable<Boolean>() {

                        @Override
                        public Boolean call() throws Exception {
                            boolean flag = sendOrder(order, bizId);
                            return flag;
                        }

                    });

            futures.add(task);
        }

        boolean result = false;
        ListenableFuture<List<Boolean>> resultsFuture = Futures.successfulAsList(futures);
        try {
            List<Boolean> resultObj = resultsFuture.get();
            if (resultObj != null && resultObj.size() > 0
                    && resultObj.stream().filter(item -> item == null || item == false).count() <= 0) {
                result = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void deliveryOrder(List<UggOrderData> orders) {
        if(orders == null) {
            logger.info("Params orders is null!");
            return ;
        }

        
        List<String> orderIds = orders.stream().
            filter(Objects :: nonNull).
            filter(item -> StringUtils.isNotEmpty(item.getTrafficNo())).
            map(item -> item.getClientOrderNo()).
            collect(Collectors.toList());
        if(orderIds == null || orderIds.isEmpty()) {
            logger.info("Orderids is empty!");
            return ;
        }

        //查询
        List<UggOrder> uggOrders = uggOrderService.getPreDeliveryOrder(orderIds);
        if(uggOrders == null || uggOrders.isEmpty()) {
            logger.info("UggOrders is empty!");
            return ;
        }

        String path = "com.mee.manage.vo.delivery.";
        //第三方发货
        //动态代理
        uggOrders.stream().filter(Objects :: nonNull).forEach(item -> {
            List<UggOrderData> uggOrder = orders.stream().filter(i -> i != null && i.getClientOrderNo() != null && i.getClientOrderNo().equals(item.getExtId())).
                collect(Collectors.toList());

            if(uggOrder == null || uggOrder.isEmpty()) {
                logger.info("UggOrderData is Empty");
                return;
            }

            UggOrderData uData = uggOrder.get(0);
            item.setExpressId(uData.getTrafficNo());
            item.setExpressName(uData.getTrafficCompanyCode());

            IDelivery delivery = deliveryContext.getDeliveryService(item.getResource());
            
            boolean flag = false;
            if( delivery != null) {
                flag = delivery.postDelivery(item, item.getBizId());
                logger.info("PostDelivery Result = {}", flag);
            } else {
                logger.info("Idelivery is null");
                flag = true;
            }

            logger.info("Flag Result = {}", flag);
            if(flag) {
                item.setStatus(UggOrderStatusEmun.Delivery.getCode());
                uggOrderService.updateById(item);
            }
            
        });
    }

    @Override
    public OrderListResult getOrders(QueryParams params, Integer pageIndex, Integer pageSize) throws MeeException {
        if (params == null) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }

        if (pageIndex == null || pageIndex == 0) {
            pageIndex = 1;
        }

        if (pageSize == null || pageSize == 0) {
            pageSize = 20;
        }

        IPage<UggOrder> pageResult = uggOrderService.getOrdersByPage(pageIndex, pageSize, params);
        if (pageResult == null) {
            throw new MeeException(StatusCode.DB_ERROR);
        }

        OrderListResult result = new OrderListResult();
        result.setPageIndex(pageResult.getCurrent());
        result.setPageSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setOrders(pageResult.getRecords());

        return result;
    }

    @Override
    public List<OrderCountResult> getOrderCount(QueryParams params) throws MeeException {
        if (params == null) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }

        List<OrderCountResult> result = uggOrderService.getOrderCount(params);

        return result;
    }

    @Override
    public boolean sendOrder(UggOrder order, Long bizId) throws MeeException {
        if (order.getProductSku() == null || order.getProductSku() <= 0) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }

        if (order.getStatus() != UggOrderStatusEmun.PrePay.getCode()) {
            throw new MeeException(StatusCode.ORDER_STATUS_ERROR);
        }

        DFOrder dfOrder = new DFOrder();
        dfOrder.setBarcode(order.getProductSku().toString());
        dfOrder.setClientOrderNo(order.getExtId());
        dfOrder.setIssubmit(1); // Issubmit 0= save only，1=sbumit
        dfOrder.setOrderQty(order.getQty());
        dfOrder.setReciverAddr(order.getReceiveAddress());
        dfOrder.setReciverName(order.getReceiveName());
        dfOrder.setReciverPhone(order.getReceivePhone());
        dfOrder.setTrafficType(1);
        dfOrder.setWarehouseNo("500001");
        dfOrder.setReciverCountryCode(100);

        logger.info(JSON.toJSONString(dfOrder));

        String token = getToken(bizId);
        if (token == null)
            throw new MeeException(StatusCode.TOKEN_ERROR);

        String url = config.getCreateDFUrl() + "?token=" + token;

        String result = JoddHttpUtils.sendPostUseBody(url, dfOrder);
        if (StringUtils.isEmpty(result)) {
            throw new MeeException(StatusCode.FAIL);
        }

        UggResult<DFOrderResp> response = JSON.parseObject(result, new TypeReference<UggResult<DFOrderResp>>() {
        });
        if (response == null || response.getCode() != 200) {
            throw new MeeException(StatusCode.FAIL);
        }

        DFOrderResp uggResult = response.getResult();
        if (uggResult == null || uggResult.getStatus() != 0) {
            throw new MeeException(StatusCode.FAIL);
        }

        return updateSent(order);
    }

    private boolean updateSent(UggOrder uggOrder) throws MeeException {
        if (uggOrder == null || uggOrder.getStatus() != UggOrderStatusEmun.PrePay.getCode()) {
            return false;
        }
        uggOrder.setStatus(UggOrderStatusEmun.PreDelivery.getCode());
        return uggOrderService.updateById(uggOrder);
    }

    @Override
    public QueryOrderRsp queryUggOrders(QueryOrder params, Long bizId) throws MeeException {
        String token = getLoginToken(bizId);
        if(token == null) {
            throw new MeeException(StatusCode.TOKEN_ERROR);
        }

        
        UggLoginResult<QueryOrderRsp> response = null;
        try {
            response = queryUggData(params, token);
        } catch (MeeException ex) {
            if (ex.getStatusCode() == StatusCode.TOKEN_ERROR) {
               token = refreshToken(bizId);
               if(token != null) {
                response = queryUggData(params, token);
               }
            }
        }

        if(response == null) {
            throw new MeeException(StatusCode.UGG_ORDER_ERROR);
        }

        QueryOrderRsp uggResult = response.getData();
        if (uggResult == null) {
            throw new MeeException(StatusCode.FAIL);
        }
        return uggResult;
    }

    private UggLoginResult<QueryOrderRsp> queryUggData(QueryOrder params, String token) throws MeeException {
        String url = config.getQueryUrl() + "?token=" + token;
        
        String result = JoddHttpUtils.sendPostUseBody(url, params);
        if (StringUtils.isEmpty(result)) {
            throw new MeeException(StatusCode.FAIL);
        }

        UggLoginResult<QueryOrderRsp> response = JSON.parseObject(result, new TypeReference<UggLoginResult<QueryOrderRsp>>() {});
        if (response == null || response.getStatus() != 0) {
            if(response.getStatus() == 403) {   //Token Error
                throw new MeeException(StatusCode.TOKEN_ERROR, response.getErrorMsg());
            } else 
                throw new MeeException(StatusCode.FAIL, response.getErrorMsg());
        }

        return response;
    }

    
}

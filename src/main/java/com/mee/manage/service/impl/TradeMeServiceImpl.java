package com.mee.manage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mee.manage.config.TradeMeConfig;
import com.mee.manage.enums.PlatFormCodeEmn;
import com.mee.manage.enums.TradeMeStatusEnum;
import com.mee.manage.exception.MeeException;
import com.mee.manage.po.PlatformConfig;
import com.mee.manage.service.IConfigurationService;
import com.mee.manage.service.IPlatformConfigService;
import com.mee.manage.service.ITradeMeService;
import com.mee.manage.util.GuavaExecutors;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.util.StatusCode;
import com.mee.manage.util.StrUtil;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.OAuthVo;
import com.mee.manage.vo.trademe.SoltItem;
import com.mee.manage.vo.trademe.SoltItemFilter;
import com.mee.manage.vo.trademe.SoltItemList;
import com.mee.manage.vo.trademe.SoltItemResponse;
import com.mee.manage.vo.trademe.TradeMeAccessToken;
import com.mee.manage.vo.trademe.TradeMeEditItemResp;
import com.mee.manage.vo.trademe.TradeMeError;
import com.mee.manage.vo.trademe.TradeMePayResult;
import com.mee.manage.vo.trademe.PurchaseItem;
import com.mee.manage.vo.trademe.TradeMeProfile;
import com.mee.manage.vo.trademe.TradeMeSoldOrderResp;
import com.mee.manage.vo.trademe.TradeMeSoltOrder;
import com.mee.manage.vo.trademe.TradeMeTokenResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeMeServiceImpl implements ITradeMeService {

    private static final Logger logger = LoggerFactory.getLogger(ITradeMeService.class);

    @Autowired
    IConfigurationService configurationService;

    @Autowired
    TradeMeConfig tradeMeConfig;

    @Autowired
    IPlatformConfigService platService;

    @Override
    public MeeResult checkToken() {

        return null;
    }

    @Override
    public TradeMeTokenResult requestToken(Long bizId) throws MeeException {

        String url = tradeMeConfig.getTokenUrl();
        Map<String, String> params = new HashMap<>();
        params.put("scope", "MyTradeMeRead,MyTradeMeWrite");

        OAuthVo oAuth = new OAuthVo();
        oAuth.setConsumerKey(tradeMeConfig.getKey());
        oAuth.setConsumerSecret(tradeMeConfig.getSecret());
        oAuth.setSignatureMethod(OAuthVo.SIGN_METHOD_PLAINTEXT);
        oAuth.setCallbackUrl(tradeMeConfig.getCallbackUrl());

        String result = JoddHttpUtils.getDataWithAuth(url, params, oAuth);
        if (result == null)
            throw new MeeException(StatusCode.FAIL);

        Map<String, Object> map = StrUtil.getUrlParams(result);
        TradeMeTokenResult tokenResult = new TradeMeTokenResult();
        tokenResult.setOauth_token(map.get("oauth_token").toString());
        tokenResult.setOauth_token_secret(map.get("oauth_token_secret").toString());
        tokenResult.setOauth_callback_confirmed(map.get("oauth_callback_confirmed").toString());

        PlatformConfig entity = new PlatformConfig();
        entity.setBizId(bizId);
        entity.setName("TradeMe");
        entity.setPlatformCode(PlatFormCodeEmn.TRADEMEE.getCode());
        entity.setClientSecret(tokenResult.getOauth_token_secret());
        entity.setToken(tokenResult.getOauth_token());

        platService.save(entity);
        return tokenResult;
    }

    @Override
    public boolean accessToken(Long bizId, TradeMeAccessToken accessToken) throws MeeException {
        PlatformConfig entity = platService.getPlatFormByToken(bizId, PlatFormCodeEmn.TRADEMEE.getCode(),
                accessToken.getToken());
        if (entity == null)
            return false;

        String url = tradeMeConfig.getAccessTokenUrl();
        OAuthVo oAuth = new OAuthVo();
        oAuth.setConsumerKey(tradeMeConfig.getKey());
        oAuth.setSignatureMethod(OAuthVo.SIGN_METHOD_PLAINTEXT);
        oAuth.setAccessToken(accessToken.getToken());
        oAuth.setVerifier(accessToken.getTokenVerifier());
        oAuth.setConsumerSecret(tradeMeConfig.getSecret());
        oAuth.setTokenSecret(entity.getClientSecret());

        String result = JoddHttpUtils.getDataWithAuth(url, oAuth);
        if (result == null)
            throw new MeeException(StatusCode.FAIL);

        Map<String, Object> map = StrUtil.getUrlParams(result);
        String token = map.get("oauth_token").toString();
        String token_secret = map.get("oauth_token_secret").toString();

        entity.setToken(token);
        entity.setClientSecret(token_secret);
        boolean flag = false;
        TradeMeProfile profile = getProfile(bizId, entity);
        if (profile != null) {
            entity.setName(profile.getFirstName() + ' ' + profile.getLastName());
            flag = platService.updateById(entity);
        }
        return flag;
    }

    @Override
    public TradeMeProfile getProfile(Long bizId, Integer platFormId) throws MeeException {
        PlatformConfig entity = platService.getPlatFormById(platFormId);
        if (entity == null) {
            throw new MeeException(StatusCode.PLATFORM_NOT_EXIST);
        }
        String url = tradeMeConfig.getProfileUrl();
        OAuthVo oAuthVo = getAuthVo(entity);
        if (oAuthVo == null)
            return null;

        String result = JoddHttpUtils.getDataWithAuth(url, oAuthVo);
        if (result == null)
            throw new MeeException(StatusCode.FAIL);

        TradeMeProfile profile = JSON.parseObject(result, TradeMeProfile.class);

        return profile;
    }

    public TradeMeProfile getProfile(Long bizId, PlatformConfig entity) throws MeeException {
        String url = tradeMeConfig.getProfileUrl();
        OAuthVo oAuthVo = getAuthVo(entity);
        if (oAuthVo == null)
            return null;

        String result = JoddHttpUtils.getDataWithAuth(url, oAuthVo);
        if (result == null)
            throw new MeeException(StatusCode.FAIL);

        TradeMeProfile profile = JSON.parseObject(result, TradeMeProfile.class);

        return profile;
    }

    private OAuthVo getAuthVo(PlatformConfig entity) throws MeeException {
        OAuthVo oAuth = new OAuthVo();
        oAuth.setConsumerKey(tradeMeConfig.getKey());
        oAuth.setAccessToken(entity.getToken());
        oAuth.setSignatureMethod(OAuthVo.SIGN_METHOD_PLAINTEXT);
        oAuth.setConsumerSecret(tradeMeConfig.getSecret());
        oAuth.setTokenSecret(entity.getClientSecret());
        return oAuth;
    }

    @Override
    public TradeMeSoldOrderResp getSoltItem(Integer platFormId, SoltItemFilter filter) throws MeeException {
        PlatformConfig entity = platService.getPlatFormById(platFormId);
        if(entity == null) {
            throw new MeeException(StatusCode.PLATFORM_NOT_EXIST);
        }

        String url = tradeMeConfig.getSoltItemUrl() + "/" + filter.getFilter() + ".JSON";

        OAuthVo oAuthVo = getAuthVo(entity);

        int page = 1;

        List<SoltItemList> items = new ArrayList<>();
        getAllSoltItem(page,url,items,oAuthVo);

        if(items == null || items.size() <= 0)
            return null;
        
        TradeMeSoldOrderResp orders = getSoltOrder(items, filter);


        return orders;
    }

    private TradeMeSoldOrderResp getSoltOrder(List<SoltItemList> items, SoltItemFilter filter) {
        if(items == null || items.size() <= 0)
            return null;
        
        TradeMeSoldOrderResp resp = new TradeMeSoldOrderResp();
        if(filter != null) {
            List<SoltItemList> mailSendItems = items.stream().filter(item -> ((item.getStatus() == null || item.getStatus() == 0 || item.getStatus() == 10 ) && (item.getPaymentMethod() == null || item.getPaymentMethod() != 32 ))).collect(Collectors.toList());
            resp.setEmailSent(getSoltOrder(mailSendItems));

            List<SoltItemList> paidItems = items.stream().filter(
                item -> ((item.getStatus() != null && item.getStatus() == 20) || 
                    (item.getPaymentMethod() != null && item.getPaymentMethod() == 32 && (item.getStatus() == null || item.getStatus() != 30 && item.getStatus() != 40)))).
                    collect(Collectors.toList());
            resp.setPaymentReceived(getSoltOrder(paidItems));
            
            List<SoltItemList> ShippedItems = items.stream().filter(item -> item.getStatus() != null && item.getStatus() == 30 ).collect(Collectors.toList());
            resp.setGoodsShipped(getSoltOrder(ShippedItems));
            
            List<SoltItemList> SaleCompleted = items.stream().filter(item -> item.getStatus() != null && item.getStatus() == 40 ).collect(Collectors.toList());
            resp.setSaleCompleted(getSoltOrder(SaleCompleted));
        }
        
        
        return resp;
    }

    private List<TradeMeSoltOrder> getSoltOrder(List<SoltItemList> items) {
        if( items == null || items.size() <= 0)
            return null;

        List<TradeMeSoltOrder> orders = new ArrayList<>();
        for (SoltItemList entry : items) {
            TradeMeSoltOrder orderInfor = new TradeMeSoltOrder();
            orderInfor.setOrderId(entry.getOrderId());
            orderInfor.setPurchaseId(entry.getPurchaseId());
            orderInfor.setReference(entry.getReferenceNumber());
            orderInfor.setBuyer(entry.getBuyer());
            orderInfor.setDeliveryAddress(entry.getDeliveryAddress());
            orderInfor.setSoldDate(entry.getSoldDate());
            orderInfor.setItems(gItems(entry));
            orderInfor.setPaymentDetail(entry.getPaymentDetails());
            orders.add(orderInfor);
        }
        return orders;
    }

    private SoltItem gItems(SoltItemList soltItem) {
        SoltItem item = new SoltItem();
        item.setName(soltItem.getTitle());
        item.setPhoto(soltItem.getPictureHref());
        item.setQuantity(soltItem.getQuantitySold());
        item.setSku(soltItem.getSKU());
        item.setPrice(soltItem.getTotalSalePrice());
        return item;
    }

    private void getAllSoltItem(int page, String url,List<SoltItemList> items,OAuthVo oAuthVo) {
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("page", page + "");
        queryParam.put("rows", "50");

        String result = JoddHttpUtils.getDataWithAuth(url, queryParam ,oAuthVo);
        if (result == null)
            throw new MeeException(StatusCode.FAIL);
        
        if(result.indexOf("ErrorDescription") >= 0) {
            TradeMeError tradeMeError = JSON.parseObject(result, TradeMeError.class);
            if(tradeMeError == null) {
                throw new MeeException(StatusCode.FAIL);
            } else if(tradeMeError.getErrorDescription().
                equals("You have exceeded your API call quota for the current hour.")) {
                throw new MeeException(StatusCode.TRADEME_EXCEED_QUOTA);
            } else {
                throw new MeeException(StatusCode.FAIL);
            }
        }
        
        SoltItemResponse response = JSON.parseObject(result, SoltItemResponse.class);
        if(response == null || response.getList() == null || response.getList().size() <= 0)
            return ;
        
        if(items == null) {
            items = new ArrayList<>();
        }

        items.addAll(response.getList());
        if(response.getPageSize() > 0 && response.getPageSize() > 50) {
            getAllSoltItem(++page, url, items, oAuthVo);
        }
    }

    @Override
    public List<TradeMePayResult> paidItem(Integer platFormId, PurchaseItem items) {
        return editItem(platFormId, items, TradeMeStatusEnum.PaymentReceived);
    }   

    public List<TradeMePayResult> editItem(Integer platFormId, PurchaseItem items, TradeMeStatusEnum tradeMeStatusEnum) {
        List<String> purchaseIds = items.getPurchaseId();
        if(purchaseIds == null || purchaseIds.size() <= 0) {
            throw new MeeException(StatusCode.PARAM_ERROR);
        }
        PlatformConfig entity = platService.getPlatFormById(platFormId);
        if(entity == null) {
            throw new MeeException(StatusCode.PLATFORM_NOT_EXIST);
        }

        OAuthVo oAuthVo = getAuthVo(entity);
        if(oAuthVo == null) {
            throw new MeeException(StatusCode.TRADEME_EXCEED_QUOTA);

        } 

        List<ListenableFuture<TradeMePayResult>> futures = Lists.newArrayList();

        for(String purchaseId : purchaseIds) {
            ListenableFuture<TradeMePayResult> task = GuavaExecutors.getDefaultCompletedExecutorService()
                    .submit(new Callable<TradeMePayResult>() {

                        @Override
                        public TradeMePayResult call() throws Exception {
                            boolean flag = editItem(oAuthVo, purchaseId, tradeMeStatusEnum);
                            TradeMePayResult result = new TradeMePayResult();
                            result.setPurchaseId(purchaseId);
                            result.setResult(flag);
                            return result;
                        }

                    });

            futures.add(task);
        }


        ListenableFuture<List<TradeMePayResult>> resultsFuture = Futures.successfulAsList(futures);
        List<TradeMePayResult> resultObj = null;
        try {
            resultObj = resultsFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("EditStatus Error", e);

        } catch (ExecutionException e) {
            e.printStackTrace();
            logger.error("EditStatus Exp", e);
        }

        return resultObj;
    }


    private boolean editItem(OAuthVo oAuth, String purchaseId, TradeMeStatusEnum tradeMeStatusEnum) {
        String url = tradeMeConfig.getEditStatusUrl() + '/' + purchaseId + '/' + tradeMeStatusEnum.name() + ".JSON";
        String result = JoddHttpUtils.sendPostWithAuth(url, null, oAuth);
        if(result == null)
            return false;

        TradeMeEditItemResp resp = JSON.parseObject(result,TradeMeEditItemResp.class);
        if(resp == null) {
            return false;
        }

        return resp.getSuccess();
    }
}

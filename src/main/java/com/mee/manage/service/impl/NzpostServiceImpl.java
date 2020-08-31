package com.mee.manage.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mee.manage.config.NzPostConfig;
import com.mee.manage.enums.PlatFormCodeEmn;
import com.mee.manage.exception.MeeException;
import com.mee.manage.po.NzpostConfig;
import com.mee.manage.po.PlatformConfig;
import com.mee.manage.service.INzpostConfigService;
import com.mee.manage.service.INzpostService;
import com.mee.manage.service.IPlatformConfigService;
import com.mee.manage.util.DateUtil;
import com.mee.manage.util.GuavaExecutors;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.JoddResponse;
import com.mee.manage.vo.nzpost.CreateLabelRep;
import com.mee.manage.vo.nzpost.LabelStatusResult;
import com.mee.manage.vo.nzpost.LabelTrack;
import com.mee.manage.vo.nzpost.Labels;
import com.mee.manage.vo.nzpost.NzDeliveryAddress;
import com.mee.manage.vo.nzpost.NzDimension;
import com.mee.manage.vo.nzpost.NzParcelDetails;
import com.mee.manage.vo.nzpost.NzPickupAddress;
import com.mee.manage.vo.nzpost.NzPostSendDetails;
import com.mee.manage.vo.nzpost.NzReceiverDetails;
import com.mee.manage.vo.nzpost.NzpostLabelReq;
import com.mee.manage.vo.nzpost.NzpostTokenRsp;
import com.mee.manage.vo.nzpost.ShippedOptionReq;
import com.mee.manage.vo.nzpost.ShippedOptionResp;
import com.mee.manage.vo.nzpost.ShippedOptionService;
import com.mee.manage.vo.nzpost.NzPostLabelResult;
import com.mee.manage.vo.nzpost.NzPostLabelStatus;
import com.mee.manage.vo.trademe.ShippedDimensions;
import com.mee.manage.vo.trademe.ShippedItem;
import com.mee.manage.vo.trademe.ShippedPurchase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NzpostServiceImpl implements INzpostService {

    protected static final Logger logger = LoggerFactory.getLogger(INzpostService.class);

    @Autowired
    NzPostConfig config;

    @Autowired
    IPlatformConfigService platFormService;

    @Autowired
    INzpostConfigService nzPostConfigService;

    @Override
    public String getToken(Long bizId) {
        String clientId = config.getClientId();
        String secret = config.getSecret();
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", secret);
        params.put("grant_type", "client_credentials");
        String result = JoddHttpUtils.sendPost(config.getAccessTokenUrl(), params);
        if (result == null)
            return null;

        String token = null;
        NzpostTokenRsp tokenRsp = JSON.parseObject(result, NzpostTokenRsp.class);
        if (tokenRsp != null) {
            token = tokenRsp.getAccess_token();
            PlatformConfig platConfig = platFormService.getOnePlatForm(bizId, PlatFormCodeEmn.NZ_POST.getCode());
            if (platConfig == null) {
                platConfig = new PlatformConfig();
                platConfig.setBizId(bizId);
                platConfig.setClientId(clientId);
                platConfig.setClientSecret(secret);
                platConfig.setToken(token);
                platConfig.setPlatformCode(PlatFormCodeEmn.NZ_POST.getCode());
                platConfig.setName(PlatFormCodeEmn.NZ_POST.getCode());
                Date expireDate = DateUtil.getSuffixSecond(Integer.parseInt(tokenRsp.getExpires_in()));
                platConfig.setExpire(expireDate);

                platFormService.save(platConfig);
            } else {
                platConfig.setBizId(bizId);
                platConfig.setClientId(clientId);
                platConfig.setClientSecret(secret);
                platConfig.setToken(token);
                Date expireDate = DateUtil.getSuffixSecond(Integer.parseInt(tokenRsp.getExpires_in()));
                platConfig.setExpire(expireDate);
                platConfig.setPlatformCode(PlatFormCodeEmn.NZ_POST.getCode());
                platConfig.setName(PlatFormCodeEmn.NZ_POST.getCode());
                platFormService.updateById(platConfig);
            }

        }
        return token;
    }

    @Override
    public List<NzPostLabelResult> shippedItem(Long nzPostId, ShippedItem items) throws MeeException {
        String token = getToken();
        if (token == null) {
            throw new MeeException(StatusCode.NZPOST_TOKEN_ERROR);
        }

        final String fToken = token;

        NzpostConfig nzPostConfig = nzPostConfigService.getById(nzPostId);
        if (nzPostConfig == null) {
            throw new MeeException(StatusCode.NZPOST_CONFIG_NOT_EXIST);
        }

        List<NzpostLabelReq> request = getNzPostLabelRq(nzPostConfig, items);
        if (request == null) {
            return null;
        }

        List<ListenableFuture<NzPostLabelResult>> futures = Lists.newArrayList();
        String url = config.getCreateLabelUrl();
        String clientId = config.getClientId();
        Map<String, String> header = new HashMap<>();
        header.put("client_id", clientId);
        for (NzpostLabelReq req : request) {
            ListenableFuture<NzPostLabelResult> task = GuavaExecutors.getDefaultCompletedExecutorService()
                    .submit(new Callable<NzPostLabelResult>() {

                        @Override
                        public NzPostLabelResult call() throws Exception {
                            NzPostLabelResult tResult = new NzPostLabelResult();
                            tResult.setPurchaseId(req.getSender_reference_2());

                            JoddResponse result = JoddHttpUtils.sendPostUseResp(url, JSON.toJSONString(req), fToken,
                                    header);
                            if (result == null) {
                                tResult.setResult(false);
                            } else if (result.getStatus() == 401) {
                                logger.info("Invalid client ID", result);
                                tResult.setResult(false);
                            } else if (result.getStatus() == 403) {
                                logger.info("Status: 403", result);
                                tResult.setResult(false);
                            } else if (result.getStatus() == 200) {
                                CreateLabelRep response = JSON.parseObject(result.getBodytext(), CreateLabelRep.class);
                                if (response == null || !response.isSuccess()) {
                                    tResult.setResult(false);
                                } else {
                                    tResult.setConsignmentId(response.getConsignment_id());
                                    tResult.setResult(true);
                                }
                            } else {
                                logger.info("Status", result);
                                tResult.setResult(false);
                            }
                            return tResult;
                        }

                    });

            futures.add(task);
        }

        List<NzPostLabelResult> resultObj = null;
        ListenableFuture<List<NzPostLabelResult>> resultsFuture = Futures.successfulAsList(futures);
        try {
            resultObj = resultsFuture.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("UpdatePrice Total Error", e);

        } catch (ExecutionException e) {
            e.printStackTrace();
            logger.error("UpdatePrice Total Exp", e);

        }

        return resultObj;
    }

    private List<NzpostLabelReq> getNzPostLabelRq(NzpostConfig nzPostConfig, ShippedItem items) {
        if (nzPostConfig == null || items == null) {
            return null;
        }

        NzPostSendDetails sendDetail = new NzPostSendDetails();
        sendDetail.setCompany_name(nzPostConfig.getCompanyName());
        sendDetail.setEmail(nzPostConfig.getEmail());
        sendDetail.setName(nzPostConfig.getName());
        sendDetail.setPhone(nzPostConfig.getPhone());
        sendDetail.setSite_code(config.getSiteCode());

        NzPickupAddress pickupAddress = new NzPickupAddress();
        pickupAddress.setCity(nzPostConfig.getCity());
        pickupAddress.setCompany_name(nzPostConfig.getCompanyName());
        pickupAddress.setCountry_code(nzPostConfig.getCountryCode());
        pickupAddress.setPostcode(nzPostConfig.getPostcode());
        pickupAddress.setStreet(nzPostConfig.getStreet());
        pickupAddress.setSuburb(nzPostConfig.getSuburb());

        List<NzpostLabelReq> labelReqs = new ArrayList<>();

        for (ShippedPurchase purchas : items.getPurches()) {
            NzReceiverDetails receiverDetail = new NzReceiverDetails();
            receiverDetail.setName(purchas.getDeliveryName());
            receiverDetail.setPhone(purchas.getDeliveryPhone());
            receiverDetail.setEmail(purchas.getDeliveryEmail());

            NzDeliveryAddress deliveryAddress = new NzDeliveryAddress();
            deliveryAddress.setCity(purchas.getCity());
            deliveryAddress.setCountry_code(purchas.getCountryCode());
            deliveryAddress.setPostcode(purchas.getPostcode());
            deliveryAddress.setStreet(purchas.getStreet());
            deliveryAddress.setSuburb(purchas.getSuburb());

            List<NzParcelDetails> parcelDetails = new ArrayList<>();
            for (ShippedDimensions dimension : purchas.getDimensions()) {
                NzParcelDetails detail = new NzParcelDetails();
                detail.setService_code(dimension.getServiceCode());
                detail.setAdd_ons(dimension.getAddOns());
                detail.setDescription(null);
                detail.setReturn_indicator("OUTBOUND");
                NzDimension dimensions = new NzDimension();
                // dimensions.setVolume_m3(dimension.getVolumes());

                dimensions.setWeight_kg(dimension.getWeight());
                dimensions.setHeight_cm(dimension.getHeight());
                dimensions.setWidth_cm(dimension.getWidth());
                dimensions.setLength_cm(dimension.getLength());

                detail.setDimensions(dimensions);

                parcelDetails.add(detail);
            }

            NzpostLabelReq labelRequest = new NzpostLabelReq();
            labelRequest.setCarrier(purchas.getCarrier().toUpperCase());
            labelRequest.setSender_reference_1(nzPostConfig.getReference());
            labelRequest.setSender_reference_2(purchas.getOrderId());
            labelRequest.setParcel_details(parcelDetails);
            labelRequest.setPickup_address(pickupAddress);
            labelRequest.setReceiver_details(receiverDetail);
            labelRequest.setDelivery_address(deliveryAddress);
            labelRequest.setSender_details(sendDetail);

            labelReqs.add(labelRequest);
        }
        return labelReqs;
    }

    @Override
    public List<ShippedOptionService> shippedItem(ShippedOptionReq entity) throws MeeException {
        String token = getToken();
        String clientId = config.getClientId();
        String url = config.getShippedOptionUrl();

        Map<String, String> params = new HashMap<>();
        params.put("weight", entity.getWeight());
        params.put("length", entity.getLength());
        params.put("width", entity.getWidth());
        params.put("height", entity.getHeight());

        params.put("pickup_suburb", entity.getPickup().getSuburb());
        params.put("pickup_city", entity.getPickup().getCity());
        params.put("pickup_postcode", entity.getPickup().getPostcode());

        params.put("delivery_suburb", entity.getDelivery().getSuburb());
        params.put("delivery_city", entity.getDelivery().getCity());
        params.put("delivery_postcode", entity.getDelivery().getPostcode());

        Map<String, String> header = new HashMap<>();
        header.put("client_id", clientId);

        String result = JoddHttpUtils.getData(url, params, token, header);
        if (result == null) {
            throw new MeeException(StatusCode.FAIL);
        }

        ShippedOptionResp resp = JSON.parseObject(result, ShippedOptionResp.class);
        if (resp == null || !resp.getSuccess()) {
            if (resp.getErrors() != null && resp.getErrors().size() > 0) {
                throw new MeeException(StatusCode.FAIL, resp.getErrors().get(0).getMessage());
            } else {
                throw new MeeException(StatusCode.FAIL);
            }
        }
        return resp.getServices();
    }

    private String getToken() throws MeeException {
        String token = null;
        PlatformConfig pConfig = platFormService.getOnePlatForm(20L, PlatFormCodeEmn.NZ_POST.getCode());
        if (pConfig == null || pConfig.getExpire().before(new Date())) {
            token = getToken(20L);
        } else {
            token = pConfig.getToken();
        }

        if (token == null) {
            throw new MeeException(StatusCode.NZPOST_TOKEN_ERROR);
        }
        return token;
    }

    @Override
    public NzPostLabelStatus labelStatus(String consignmentId) throws MeeException {
        String token = getToken();
        String clientId = config.getClientId();
        String url = config.getStatusOfLabelUrl() + '/' + consignmentId + "/status";
        Map<String, String> header = new HashMap<>();
        header.put("client_id", clientId);

        String result = JoddHttpUtils.getData(url, null, token, header);
        if (result == null) {
            throw new MeeException(StatusCode.FAIL);
        }

        NzPostLabelStatus labelStatus = JSON.parseObject(result, NzPostLabelStatus.class);
        if (labelStatus == null) {
            throw new MeeException(StatusCode.FAIL);
        }

        return labelStatus;
    }

    @Override
    public List<LabelStatusResult> levelStatus(String[] consignmentId) throws MeeException {
        
        List<ListenableFuture<NzPostLabelStatus>> futures = Lists.newArrayList();
        for(String id: consignmentId) {
            ListenableFuture<NzPostLabelStatus> task = GuavaExecutors.getDefaultCompletedExecutorService()
                    .submit(new Callable<NzPostLabelStatus>() {

                        @Override
                        public NzPostLabelStatus call() throws Exception {
                            return labelStatus(id);
                        }

                    });

            futures.add(task);
        }

        List<LabelStatusResult> result = Lists.newArrayList();

        ListenableFuture<List<NzPostLabelStatus>> resultsFuture = Futures.successfulAsList(futures);
        try {
            List<NzPostLabelStatus> resultObj = resultsFuture.get();
            if(resultObj != null ) {
                for(NzPostLabelStatus status: resultObj) {
                    if(status.getSuccess()) {
                        LabelStatusResult flag = new LabelStatusResult();
                        flag.setConsignmentId(status.getConsignment_id());
                        List<LabelTrack> tracks = Lists.newArrayList();
                        for(Labels label : status.getLabels()) {
                            LabelTrack labelTrack = new LabelTrack();
                            labelTrack.setTrackId(label.getTracking_reference());
                            labelTrack.setStatus(label.getLabel_generation_status());

                            tracks.add(labelTrack);
                        }

                        flag.setTracks(tracks);
                        result.add(flag);
                    }
                }
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


}
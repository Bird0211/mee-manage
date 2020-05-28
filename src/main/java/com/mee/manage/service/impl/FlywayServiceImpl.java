package com.mee.manage.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mee.manage.config.FlywayConfig;
import com.mee.manage.enums.PlatFormCodeEmn;
import com.mee.manage.exception.MeeException;
import com.mee.manage.po.PlatformConfig;
import com.mee.manage.service.IConfigurationService;
import com.mee.manage.service.IFlywayService;
import com.mee.manage.service.IPlatformConfigService;
import com.mee.manage.util.JWTUtil;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.FlywayOrderResponse;
import com.mee.manage.vo.FlywayorderDetail;
import com.mee.manage.vo.FlywayorderVo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class FlywayServiceImpl implements IFlywayService {

    protected static final Logger logger = LoggerFactory.getLogger(IFlywayService.class);

    private static final String FIYWAY = "flyway";


    @Autowired
    FlywayConfig flywayConfig;

    @Autowired
    IPlatformConfigService platformService;

    @Autowired
    IConfigurationService configService;


    @Override
    public boolean authToken(Long bizId, String username, String password) {
        String url = flywayConfig.getTokenUrl();
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        
        String result = JoddHttpUtils.sendPostUseBody(url, params);
        if(StringUtils.isEmpty(result))
            return false;

        logger.info("result = {}",result);

        if(result.equals("\"Wrong password\"")) {
            logger.info("Wrong password = {}", result);
            return false;
        }

        JSONObject jObject = JSON.parseObject(result);
        String jsonToken = jObject.getString("jsonWebToken");

        if(StringUtils.isEmpty(jsonToken)) {
            return false;
        }

        String icID = getIcId(jsonToken);
        if(icID == null)
            return false;
        
        

        PlatformConfig pConfig = new PlatformConfig();
        pConfig.setBizId(bizId);
        pConfig.setName("FLYWAY");
        pConfig.setToken(jsonToken);
        pConfig.setPlatformCode(PlatFormCodeEmn.FLYWAY.getCode());
        pConfig.setClientId(icID);
        pConfig.setExpire(getExpDate(jsonToken));
        
        boolean flag = platformService.updatePlatFormCode(pConfig);
        if(flag) {
            setNamePwd(bizId, username, password);
        }
    
        return flag;
    }

    private String getIcId(String jsonWebToken) {
        Map<String,String> result = JWTUtil.decode(jsonWebToken);
        if(result != null) 
            return result.get("icID");
        return null;
    }

    private Date getExpDate(String jsonWebToken) {
        Date date = null;
        Map<String,String> result = JWTUtil.decode(jsonWebToken);
        if(result != null) {
            String expTime = result.get("exp");
            date = new Date(Long.parseLong(expTime) * 1000L);
        }
           
        return date;
    }

    @Override
    public List<FlywayOrderResponse> addOrders(Long bizId, List<FlywayorderDetail> orderDetailList) throws MeeException {
        if(!checkAuth(bizId)) {
            String[] userpwd = getNamePwd(bizId);
            if(userpwd == null) {
                throw new MeeException(StatusCode.FLYWAY_NOT_EXIST);
            } else {
                boolean flag = authToken(bizId, userpwd[0], userpwd[1]);
                if(!flag) {
                    throw new MeeException(StatusCode.FLYWAY_LOGIN_ERROR);
                }
            }
        }

        PlatformConfig config = getPlatformConfig(bizId);
        if (config == null)
            throw new MeeException(StatusCode.FLYWAY_NOT_EXIST);

        FlywayorderVo flyway = new FlywayorderVo();
        flyway.setClientID(Integer.parseInt(config.getClientId()));
        flyway.setOrderDetailList(orderDetailList);

        String url = flywayConfig.getAddOrderUrl();

        String result = JoddHttpUtils.sendPostUseBody(url, JSON.toJSONString(flyway),config.getToken());
        logger.info(result);

        List<FlywayOrderResponse> oResponses = JSON.parseArray(result, FlywayOrderResponse.class);
        if(oResponses == null || oResponses.isEmpty())
            throw new MeeException(StatusCode.FLYWAY_LOGIN_ERROR);
        
        return oResponses;
    }
    

    @Override
    public boolean checkAuth(Long bizId) {
        PlatformConfig config = platformService.getOnePlatForm(bizId, PlatFormCodeEmn.FLYWAY.getCode());
        logger.info("Config = {}", config);
        if(config == null || config.getExpire().before(new Date()))
            return false;
        
        return true;
    }

    @Override
    public PlatformConfig getPlatformConfig(Long bizId) {
        PlatformConfig config = platformService.getOnePlatForm(bizId, PlatFormCodeEmn.FLYWAY.getCode());
        if(config == null || config.getExpire().before(new Date()))
            return null;

        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setNamePwd(Long bizId, String username, String password) {
        boolean flagUser = false;
        boolean flagPwd = false;
        if(getNamePwd(bizId) == null) {
            flagUser = configService.insertConfig(FIYWAY + "_username_" + bizId, username, null);
            flagPwd = configService.insertConfig(FIYWAY + "_password_" + bizId, password, null);
        } else {
            flagUser = configService.updateConfig(FIYWAY + "_username_" + bizId, username, null);
            flagPwd = configService.updateConfig(FIYWAY + "_password_" + bizId, password, null);
        }

        if(!flagUser || !flagPwd) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return flagUser && flagPwd;
    }

    @Override
    public String[] getNamePwd(Long bizId) {
        String userName = configService.getValue(FIYWAY + "_username_" + bizId);
        String password = configService.getValue(FIYWAY + "_password_" + bizId);
        String[] result = null;
        if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            result = new String[]{userName,password};
        }
        return result;
    }
}


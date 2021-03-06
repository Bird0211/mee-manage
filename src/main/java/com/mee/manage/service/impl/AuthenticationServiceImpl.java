package com.mee.manage.service.impl;

import com.mee.manage.po.Biz;
import com.mee.manage.service.IAuthenticationService;
import com.mee.manage.service.IBizService;
import com.mee.manage.service.IConfigurationService;
import com.mee.manage.service.IUserService;
import com.mee.manage.util.DateUtil;
import com.mee.manage.util.StatusCode;
import com.mee.manage.config.MeeConfig;
import com.mee.manage.vo.AuthenticationVo;
import com.mee.manage.vo.Yiyun.YiyunUserData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(IAuthenticationService.class);


    @Autowired
    IConfigurationService configService;

    @Autowired
    IUserService userService;

    @Autowired
    IBizService bizService;

    private final int DEFAULT_TIME_LENG = 13;

    @Override
    public StatusCode checkAuth(AuthenticationVo auth) {
        if(auth == null || auth.isEmpty()) {
            logger.info("Auth is Null");
            return StatusCode.PARAM_ERROR;
        }

        //time
        if (DateUtil.getPrefixHour(1).after(new Date(Long.parseLong(addZeroForNum(auth.getTime(),DEFAULT_TIME_LENG))))) {
            logger.info("Time error Date = {}, {}",
                    new Date(Long.parseLong(addZeroForNum(auth.getTime(),DEFAULT_TIME_LENG))),
                    DateUtil.getPrefixHour(1));
            logger.info("Time error Time = {}, {}",addZeroForNum(auth.getTime(),DEFAULT_TIME_LENG),
                    DateUtil.getPrefixHour(1).getTime());
            return StatusCode.OVER_TIME;
        }
        //getToken
        String token = getMeeToken(auth.getBizId());
        if(token == null) {
            logger.info("Token is not exist",auth.getBizId());
            return StatusCode.TOKEN_ERROR;
        }
        String sign = MeeConfig.getMeeUserSign(auth.getBizId(),auth.getUserId(),
                                            Long.parseLong(auth.getTime()),
                                            token,
                                            auth.getNonce());

        logger.info("Token = {},Sign = {}",token,sign);
        boolean isSign = sign.equals(auth.getSign());
        if(!isSign) {
            return StatusCode.SIGN_ERROR;
        }

        Biz bizInfo = bizService.getBiz(Long.parseLong(auth.getBizId()));
        if(bizInfo == null) {
            return StatusCode.BIZ_NOT_EXIST;
        }

        if(bizInfo.getStatus() == 1) {
            return StatusCode.BIZ_STATUS_ERROR;
        }

        if(bizInfo.getExpireDate() != null && bizInfo.getExpireDate().before(new Date())) {
            return StatusCode.OVER_TIME;
        }
        
        if(!StringUtils.isEmpty(auth.getUserId())) {
            YiyunUserData userData = userService.getYiyunUser(auth.getBizId(),auth.getUserId());
            if(userData == null) {
                return StatusCode.USER_NOT_EXIST;
            }
        }
        
        return StatusCode.SUCCESS;
    }

    @Override
    public String getMeeToken(String bizId) {
        if(StringUtils.isEmpty(bizId))
            return null;
        //getToken
        Biz biz = bizService.getBiz(Long.parseLong(bizId));
        if (biz == null) {
            return null;
        }

        return biz.getToken();
    }


    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
//            sb.append("0").append(str);// 左补0
             sb.append(str).append("0");//右补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }

}


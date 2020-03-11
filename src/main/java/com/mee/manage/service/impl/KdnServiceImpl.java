package com.mee.manage.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.mee.manage.config.Config;
import com.mee.manage.enums.WeimobDeliveryCompany;
import com.mee.manage.service.IkdnService;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.util.MD5Util;
import com.mee.manage.vo.KdnShipperCodeVo;
import com.mee.manage.vo.KdnVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class KdnServiceImpl implements IkdnService {

    private static final Logger logger = LoggerFactory.getLogger(IkdnService.class);

    @Autowired
    Config config;

    @Override
    public WeimobDeliveryCompany identifyOrder(String order) {
        Map<String,String> param = new HashMap<>();
        param.put("LogisticCode",order);

        String result = JoddHttpUtils.sendPost(config.getKdnNumberIdentifyUrl(),getParams(param,"2002"));
        logger.info("result = {}",result);
        if (StringUtils.isEmpty(result))
            return null;

        KdnVo kdnVo = JSON.parseObject(result,KdnVo.class);
        if(kdnVo == null || !kdnVo.isSuccess() || kdnVo.getShipperCode() == null || kdnVo.getShipperCode().isEmpty())
            return null;

        List<KdnShipperCodeVo> shipperCodes = kdnVo.getShipperCode();
        WeimobDeliveryCompany company = null;
        for (KdnShipperCodeVo codeVo : shipperCodes) {
            company = WeimobDeliveryCompany.getExpCompany(codeVo.getShipperCode());
            if(company != null)
                break;
        }

        return company;
    }

    private Map<String,Object> getParams(Map<String,String> param,String requestType) {
        Map<String,Object> sdnVo = new HashMap<>();
        sdnVo.put("RequestData",JSON.toJSONString(param));
        sdnVo.put("EBusinessID",config.getKdnUserId());
        sdnVo.put("RequestType",requestType);
        sdnVo.put("DataSign",getSign(param));
        sdnVo.put("DataType","2");

        return sdnVo;
    }


    private String getSign(Map<String,String> params){

        String jsonStr = JSON.toJSONString(params) + config.getKdnKey();
        String md5 = MD5Util.MD5Encode(jsonStr,"UTF-8",false);
        String sign = null;

        try {
            sign = Base64.getEncoder().encodeToString(md5.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return sign;

    }
}

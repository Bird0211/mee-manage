package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.mee.manage.config.Config;
import com.mee.manage.enums.WeimobDeliveryCompany;
import com.mee.manage.service.IExpressService;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.vo.ExpComCodeAutoVo;
import com.mee.manage.vo.ExpComCodeVo;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ExpressServiceImpl implements IExpressService {

    private static final Logger logger = LoggerFactory.getLogger(IExpressService.class);


    @Autowired
    Config config;


    @Override
    public WeimobDeliveryCompany getExpressComByCode(String code) {
        if(StringUtils.isEmpty(code))
            return null;

        logger.info("ExpressCom code = {}",code);
        Map<String,Object> param = new HashMap<>();
        param.put("num",code);
        param.put("key",config.getExpreeKey());

        String url = config.getExpressUrl();
        String result = JoddHttpUtils.sendPost(url,param);
        if (StringUtils.isEmpty(result))
            return null;

        logger.info(result);
        List<ExpComCodeAutoVo> autoVo = JSON.parseArray(result,ExpComCodeAutoVo.class);
        if(autoVo == null || autoVo.isEmpty())
            return null;

        WeimobDeliveryCompany company  = null;
        for (ExpComCodeAutoVo auto : autoVo) {
            String comCode = auto.getComCode();
            WeimobDeliveryCompany expCom = WeimobDeliveryCompany.getExpCompany(comCode);
            if(expCom != null)
                company = expCom;
        }
        return company;
    }
}

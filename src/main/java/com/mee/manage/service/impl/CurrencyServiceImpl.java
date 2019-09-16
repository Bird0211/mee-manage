package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mee.manage.service.ICurrencyService;
import com.mee.manage.util.Config;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.vo.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyServiceImpl implements ICurrencyService {


    @Autowired
    Config config;

    @Override
    public ExchangeRate getExangeRage(String fromCurrency, String toCurrncy) {
        if(StringUtils.isEmpty(fromCurrency) || StringUtils.isEmpty(toCurrncy))
            return null;

        String url = config.getCurrencyUrl();
        String apiKey = config.getCurrencyKey();
        String q = fromCurrency+"_"+toCurrncy;

        Map<String,String> params = new HashMap<>();
        params.put("q",q);
        params.put("apiKey",apiKey);
        params.put("compact","ultra");
        String result = JoddHttpUtils.getData(url,params);
        if(StringUtils.isEmpty(result))
            return null;

        ExchangeRate exchangeRate = null;
        JSONObject jsonObject = JSON.parseObject(result);
        if(jsonObject != null) {
            BigDecimal val = jsonObject.getBigDecimal(q);
            if(val != null) {
                exchangeRate = new ExchangeRate();
                exchangeRate.setFromCurrency(fromCurrency);
                exchangeRate.setToCurrency(toCurrncy);
                exchangeRate.setRate(val);
            }
        }
        return exchangeRate;
    }
}

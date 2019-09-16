package com.mee.manage.controller;


import com.mee.manage.service.ICurrencyService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.ExchangeRate;
import com.mee.manage.vo.MeeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class CurrencyController {

    @Autowired
    ICurrencyService currencyService;

    @RequestMapping(value = "/currency", method = RequestMethod.GET)
    public MeeResult getCurrency() {

        MeeResult meeResult = new MeeResult();
        try {
            ExchangeRate exchangeRate = currencyService.getExangeRage("NZD","CNY");
            if(exchangeRate != null){
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
                meeResult.setData(exchangeRate);
            }else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());

        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

}

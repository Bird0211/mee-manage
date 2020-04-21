package com.mee.manage.controller;

import com.mee.manage.service.INineTeenService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.OrderListResponse;
import com.mee.manage.vo.nineteen.SearchVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * NineteenController
 */
@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class NineteenController  extends BaseController{

    @Autowired
    INineTeenService nineteenService;

    @RequestMapping(value = "/nineteen/list/{platformId}", method = RequestMethod.POST)
    public MeeResult getMenuByUser(@PathVariable("platformId") Integer platformId, @RequestBody SearchVo search){
        MeeResult meeResult = new MeeResult();
        try {
            OrderListResponse response = nineteenService.queryOrderList(search, platformId);
            meeResult.setData(response);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getUserMenu Error");
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    };
    
}
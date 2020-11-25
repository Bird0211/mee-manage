package com.mee.manage.controller;

import com.mee.manage.service.IJob;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api/job")
@CrossOrigin
public class JobController extends BaseController {
    
    @Autowired
    IJob job;


    @RequestMapping(value = "/ugg/delivery", method = RequestMethod.POST)
    public MeeResult loadUggDelivery() {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            job.loadUggDelivery();

        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("load Ugg Delivery Error = {}",ex);
        }
        return meeResult;
    }

    @RequestMapping(value = "/weimob/token/check", method = RequestMethod.POST)
    public MeeResult checkWeimobToken() {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            job.refreshWeimobToken();

        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("load Ugg Delivery Error = {}",ex);
        }
        return meeResult;
    }


}

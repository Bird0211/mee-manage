package com.mee.manage.controller;

import java.util.List;

import com.mee.manage.exception.MeeException;
import com.mee.manage.service.IFlywayService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.FlywayOrderResponse;
import com.mee.manage.vo.FlywayorderDetail;
import com.mee.manage.vo.MeeResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * FlywayController
 */
@Controller
@ResponseBody
@RequestMapping("/api/flyway")
@CrossOrigin
public class FlywayController extends BaseController {

    @Autowired
    IFlywayService flywayService;

    @RequestMapping(value = "/token/{bizId}", method = RequestMethod.POST)
    public MeeResult getToken(@PathVariable("bizId") Long bizId, @RequestParam("username") String username, @RequestParam("password") String password) {
        
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = flywayService.authToken(bizId, username, password);
            if(flag)
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("getToken Error bizId = {}", bizId, ex);
        }
        return meeResult;
    }

    @RequestMapping(value = "/addorder/{bizId}", method = RequestMethod.POST)
    public MeeResult addOrder(@PathVariable("bizId") Long bizId,@RequestBody List<FlywayorderDetail> orderDetailList) {
        MeeResult meeResult = new MeeResult();
        try {
            List<FlywayOrderResponse> result = flywayService.addOrders(bizId, orderDetailList);
            if(result != null) {
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
                meeResult.setData(result);
            }
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } catch (MeeException ex) {
            meeResult.setStatusCodeDes(ex.getStatusCode());
            logger.error("addOrder Error bizId = {}", bizId,ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("addOrder Error bizId = {}", bizId,ex);
        }
        return meeResult;
    }

    @RequestMapping(value = "/check/{bizId}", method = RequestMethod.POST)
    public MeeResult checkAuth(@PathVariable("bizId") Long bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = flywayService.checkAuth(bizId);
            if(flag)
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } catch (MeeException ex) {
            meeResult.setStatusCode(ex.getStatusCode().getCode());
            logger.error("check Error bizId = {}", bizId,ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("check Error bizId = {}", bizId,ex);
        }
        return meeResult;
    }

    
}
package com.mee.manage.controller;

import java.util.List;

import com.mee.manage.exception.MeeException;
import com.mee.manage.service.ITradeMeService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.trademe.SoltItemFilter;
import com.mee.manage.vo.trademe.TradeMeAccessToken;
import com.mee.manage.vo.trademe.TradeMeProfile;
import com.mee.manage.vo.trademe.TradeMeSoltOrder;
import com.mee.manage.vo.trademe.TradeMeTokenResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api/trademe")
@CrossOrigin
public class TradeMeController extends BaseController {

    @Autowired
    ITradeMeService trademeService;

    @RequestMapping(value = "/requesttoken/{bizId}", method = RequestMethod.POST)
    public MeeResult requestToken(@PathVariable("bizId") Long bizId){
        MeeResult meeResult = new MeeResult();
        try {
            TradeMeTokenResult result = trademeService.requestToken(bizId);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
            meeResult.setData(result);

        } catch (MeeException meeEx) {
            logger.error("requestToken Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("requestToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    }

    @RequestMapping(value = "/accessToken/{bizId}", method = RequestMethod.POST)
    public MeeResult accessToken(@PathVariable("bizId") Long bizId, @RequestBody TradeMeAccessToken accessToken){
        MeeResult meeResult = new MeeResult();
        try {
            boolean result = trademeService.accessToken(bizId, accessToken);
            if(result)
                meeResult.setStatusCodeDes(StatusCode.SUCCESS);
            else 
                meeResult.setStatusCodeDes(StatusCode.FAIL);

        } catch (MeeException meeEx) {
            logger.error("requestToken Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("requestToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    }

    @RequestMapping(value = "/checkToken", method = RequestMethod.GET)
    public MeeResult checkToken(){


        return null;
    }

    @RequestMapping(value = "/profile/{bizId}/{platformId}", method = RequestMethod.GET)
    public MeeResult getprofile(@PathVariable("bizId") Long bizId, @PathVariable("platformId") Integer platformId) {
        MeeResult meeResult = new MeeResult();
        try {
            TradeMeProfile result = trademeService.getProfile(bizId, platformId);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
            meeResult.setData(result);

        } catch (MeeException meeEx) {
            logger.error("requestToken Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("requestToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    }


    @RequestMapping(value = "/sold/{platformId}", method = RequestMethod.POST)
    public MeeResult getsoldItems(@PathVariable("platformId") Integer platformId, @RequestBody SoltItemFilter filter) {

        MeeResult meeResult = new MeeResult();
        try {
            List<TradeMeSoltOrder> orders = trademeService.getSoltItem(platformId, filter);
            meeResult.setData(orders);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
        } catch (MeeException meeEx) {
            logger.error("requestToken Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("requestToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;

    } 

}

package com.mee.manage.controller;

import java.util.List;

import com.mee.manage.exception.MeeException;
import com.mee.manage.service.INzpostService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.nzpost.LabelStatusResult;
import com.mee.manage.vo.nzpost.NzPostLabelResult;
import com.mee.manage.vo.nzpost.ShippedOptionReq;
import com.mee.manage.vo.nzpost.ShippedOptionService;
import com.mee.manage.vo.trademe.ShippedItem;

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
@RequestMapping("/api/nzpost")
@CrossOrigin
public class NzPostController extends BaseController {
    
    @Autowired
    INzpostService nzPostService;

    @RequestMapping(value = "/requesttoken/{bizId}", method = RequestMethod.POST)
    public MeeResult requestToken(@PathVariable("bizId") Long bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            String token = nzPostService.getToken(bizId);
            meeResult.setData(token);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (MeeException meeEx) {
            logger.error("requestToken Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("requestToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    }

    @RequestMapping(value = "/shipped/{nzPostId}", method = RequestMethod.POST)
    public MeeResult goodsShipped(@PathVariable("nzPostId") Long nzPostId, 
                                  @RequestBody ShippedItem items) {
        MeeResult meeResult = new MeeResult();
        try {
            List<NzPostLabelResult> result = nzPostService.shippedItem(nzPostId, items);
            meeResult.setData(result);
            if(result == null) {
                meeResult.setStatusCodeDes(StatusCode.FAIL);
            } else {
                if(result.stream().filter(item -> item.getResult()).count() > 0) {
                    meeResult.setStatusCodeDes(StatusCode.SUCCESS);
                } else {
                    meeResult.setStatusCodeDes(StatusCode.FAIL);
                }
            }
        } catch (MeeException meeEx) {
            logger.error("requestToken Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());
        } catch (Exception ex) {
            logger.error("requestToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    }

    @RequestMapping(value = "/shippedoption", method = RequestMethod.POST)
    public MeeResult getShippOption(@RequestBody ShippedOptionReq shippedoption) {
        MeeResult meeResult = new MeeResult();
        try {
            List<ShippedOptionService> result = nzPostService.shippedItem(shippedoption);
            if(result == null) {
                meeResult.setStatusCodeDes(StatusCode.FAIL);
            } else {
                meeResult.setStatusCodeDes(StatusCode.SUCCESS);
                meeResult.setData(result);
            }
        } catch (MeeException meeEx) {
            logger.error("requestToken Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());
        } catch (Exception ex) {
            logger.error("requestToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    }


    @RequestMapping(value = "/labelstatus", method = RequestMethod.POST)
    public MeeResult getLabelStatus(@RequestBody String[] consignmentIds) {
        MeeResult meeResult = new MeeResult();
        try {
            List<LabelStatusResult> result = nzPostService.levelStatus(consignmentIds);
            if(result == null) {
                meeResult.setStatusCodeDes(StatusCode.FAIL);
            } else {
                meeResult.setStatusCodeDes(StatusCode.SUCCESS);
                meeResult.setData(result);
            }
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
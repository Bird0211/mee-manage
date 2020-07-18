package com.mee.manage.controller;

import java.util.List;

import com.mee.manage.exception.MeeException;
import com.mee.manage.service.INineTeenService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.OrderListResponse;
import com.mee.manage.vo.nineteen.NineTeenProductGroupVo;
import com.mee.manage.vo.nineteen.NineTeenProductParam;
import com.mee.manage.vo.nineteen.NineTeenProductResponse;
import com.mee.manage.vo.nineteen.NineTeenProductTypeVo;
import com.mee.manage.vo.nineteen.NineTeenUpdatePrice;
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
@RequestMapping("/api/nineteen")
@CrossOrigin
public class NineteenController  extends BaseController{

    @Autowired
    INineTeenService nineteenService;

    @RequestMapping(value = "/list/{platformId}", method = RequestMethod.POST)
    public MeeResult getOrderList(@PathVariable("platformId") Integer platformId, @RequestBody SearchVo search){
        MeeResult meeResult = new MeeResult();
        try {
            OrderListResponse response = nineteenService.queryOrderList(search, platformId);
            meeResult.setData(response);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getOrderList Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    };

    @RequestMapping(value = "/producttype/{platformId}/{typeId}", method = RequestMethod.GET)
    public MeeResult getProductType(@PathVariable("platformId") Integer platformId, @PathVariable("typeId") Integer typeId) {
        MeeResult meeResult = new MeeResult();
        try {
            List<NineTeenProductTypeVo> response = nineteenService.getProductType(platformId, typeId);
            meeResult.setData(response);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getOrderList Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/productgroup/{platformId}", method = RequestMethod.GET)
    public MeeResult getProductGroup(@PathVariable("platformId") Integer platformId) {
        MeeResult meeResult = new MeeResult();
        try {
            List<NineTeenProductGroupVo> response = nineteenService.getProductGroup(platformId);
            meeResult.setData(response);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getOrderList Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/products/{platformId}", method = RequestMethod.PUT)
    public MeeResult getProducts(@PathVariable("platformId") Integer platformId,  @RequestBody NineTeenProductParam param) {
        MeeResult meeResult = new MeeResult();
        try {
            NineTeenProductResponse response = nineteenService.getProduct(platformId, param);
            meeResult.setData(response);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (MeeException ex) {
            logger.error("getProducts Error", ex);
            meeResult.setStatusCodeDes(ex.getStatusCode());
        } catch (Exception ex) {
            logger.error("getProducts Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/price/update/{platformId}", method = RequestMethod.POST)
    public MeeResult setPrice(@PathVariable("platformId") Integer platformId,  @RequestBody List<NineTeenUpdatePrice> updatePrice) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean response = nineteenService.updatePrice(platformId, updatePrice);
            if(response)
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else {
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
            }

        } catch (MeeException ex) {
            logger.error("getProducts Error", ex);
            meeResult.setStatusCodeDes(ex.getStatusCode());
        } catch (Exception ex) {
            logger.error("getProducts Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }
    
}
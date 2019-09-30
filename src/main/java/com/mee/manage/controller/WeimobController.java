package com.mee.manage.controller;

import com.mee.manage.po.User;
import com.mee.manage.service.IConfigurationService;
import com.mee.manage.service.IWeimobService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class WeimobController {
    private static final Logger logger = LoggerFactory.getLogger(WeimobController.class);

    @Autowired
    IWeimobService weimobService;

    @RequestMapping(value = "/weimobCode/add", method = RequestMethod.POST)
    public MeeResult addCode(@RequestParam("code") String code) {
        logger.info("Code = {}",code);
        MeeResult meeResult = new MeeResult();
        try {
            boolean result = weimobService.addCode(code);
            if(result){
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            }else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());

        } catch (Exception ex) {
            logger.error("addCode Error code = {}", code, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/token/check", method = RequestMethod.GET)
    public MeeResult checkCode() {
        MeeResult meeResult = new MeeResult();
        try {
            CheckTokenResult result = weimobService.checkToken();
            if(result != null && result.isCuccess()){
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            }else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());

        } catch (Exception ex) {
            logger.error("checkToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/order/queryList", method = RequestMethod.POST)
    public MeeResult getOrderList(@RequestBody WeimobOrderListRequest request){
        MeeResult meeResult = null;
        try {
            meeResult = weimobService.getOrderList(request);
        } catch (Exception ex) {
            logger.error("checkToken Error", ex);
            if(meeResult == null)
                meeResult = new MeeResult();
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/classify/queryList", method = RequestMethod.GET)
    public MeeResult getClassifyInfo(){

        MeeResult meeResult = new MeeResult();
        try {
            List<WeimobGroupVo> classifyVos = weimobService.getClassifyInfo();
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(classifyVos);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/order/detail", method = RequestMethod.POST)
    public MeeResult getOrderDetal(@RequestParam("orderId") String orderId) {

        MeeResult meeResult = new MeeResult();
        try {
            WeimobOrderDetailVo weimobOrder = weimobService.getWeimobOrder(orderId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(weimobOrder);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/goods/list", method = RequestMethod.POST)
    public MeeResult getGoodList(@RequestBody GoodListQueryParameter goodlist) {

        MeeResult meeResult = new MeeResult();
        try {
            List<GoodInfoVo> goods = weimobService.getWeimobGoods(goodlist);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(goods);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    public MeeResult updatePrice(@RequestBody List<GoodPriceDetail> goods){
        MeeResult meeResult = new MeeResult();
        try {
            List<PriceUpdateResult> result = weimobService.updateWeimobPrice(goods);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(result);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/goods/sku", method = RequestMethod.POST)
    public MeeResult getWeimobBySku(@RequestParam("sku") Long sku) {
        MeeResult meeResult = new MeeResult();
        try {
            GoodInfoVo result = weimobService.getWeimobGoodBySku(sku);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(result);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/goods/refresh", method = RequestMethod.GET)
    public MeeResult refreshWeimobGood(){
        MeeResult meeResult = new MeeResult();
        try {
            boolean result = weimobService.refreshWeimob();
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(result);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

}

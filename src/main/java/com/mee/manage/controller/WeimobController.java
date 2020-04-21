package com.mee.manage.controller;

import com.mee.manage.service.IWeimobService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.*;
import com.mee.manage.vo.weimob.WeimobGroupVo;
import com.mee.manage.vo.weimob.WeimobOrderDetailVo;
import com.mee.manage.vo.weimob.WeimobOrderListRequest;
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

    @RequestMapping(value = "/weimobCode/add/{bizId}", method = RequestMethod.POST)
    public MeeResult addCode(@PathVariable("bizId") Long bizId,@RequestParam("code") String code) {
        logger.info("Code = {}",code);
        MeeResult meeResult = new MeeResult();
        try {
            boolean result = weimobService.addCode(code,bizId);
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

    @RequestMapping(value = "/token/check/{bizId}", method = RequestMethod.GET)
    public MeeResult checkCode(@PathVariable("bizId") Long bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            CheckTokenResult result = weimobService.checkToken(bizId);
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

    @RequestMapping(value = "/order/queryList/{bizId}", method = RequestMethod.POST)
    public MeeResult getOrderList(@PathVariable("bizId") Long bizId, @RequestBody WeimobOrderListRequest request){
        MeeResult meeResult = null;
        try {
            meeResult = weimobService.getOrderList(request,bizId);
        } catch (Exception ex) {
            logger.error("checkToken Error", ex);
            if(meeResult == null)
                meeResult = new MeeResult();
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/classify/queryList/{bizId}", method = RequestMethod.GET)
    public MeeResult getClassifyInfo(@PathVariable("bizId") Long bizId){

        MeeResult meeResult = new MeeResult();
        try {
            List<WeimobGroupVo> classifyVos = weimobService.getClassifyInfo(bizId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(classifyVos);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/order/detail/{bizId}", method = RequestMethod.POST)
    public MeeResult getOrderDetal(@PathVariable("bizId") Long bizId, @RequestParam("orderId") String orderId) {

        MeeResult meeResult = new MeeResult();
        try {
            WeimobOrderDetailVo weimobOrder = weimobService.getWeimobOrder(orderId,bizId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(weimobOrder);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/goods/list/{bizId}", method = RequestMethod.POST)
    public MeeResult getGoodList(@PathVariable("bizId") Long bizId,@RequestBody GoodListQueryParameter goodlist) {

        MeeResult meeResult = new MeeResult();
        try {
            List<GoodInfoVo> goods = weimobService.getWeimobGoods(goodlist,bizId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(goods);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/goods/update/{bizId}", method = RequestMethod.POST)
    public MeeResult updatePrice(@PathVariable("bizId") Long bizId,@RequestBody List<GoodPriceDetail> goods){
        MeeResult meeResult = new MeeResult();
        try {
            List<PriceUpdateResult> result = weimobService.updateWeimobPrice(goods,bizId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(result);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/goods/sku/{bizId}", method = RequestMethod.POST)
    public MeeResult getWeimobBySku(@PathVariable("bizId") Long bizId,@RequestParam("sku") Long sku) {
        MeeResult meeResult = new MeeResult();
        try {
            GoodInfoVo result = weimobService.getWeimobGoodBySku(sku,bizId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(result);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/goods/refresh/{bizId}", method = RequestMethod.GET)
    public MeeResult refreshWeimobGood(@PathVariable("bizId") Long bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean result = weimobService.refreshWeimob(bizId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(result);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/order/delivery/{bizId}", method = RequestMethod.POST)
    public MeeResult orderDelivery(@PathVariable("bizId") Long bizId,@RequestBody List<DeliveryOrderVo> deliverys) {
        MeeResult meeResult = new MeeResult();
        try {
            OrderDeliveryResult result = weimobService.orderDelivery(deliverys,bizId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(result);
        } catch (Exception ex) {
            logger.error("getClassifyInfo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;


    }



}

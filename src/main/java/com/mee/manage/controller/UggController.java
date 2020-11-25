package com.mee.manage.controller;

import java.util.List;

import com.mee.manage.exception.MeeException;
import com.mee.manage.po.UggOrder;
import com.mee.manage.service.IUggService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.ugg.OrderCountResult;
import com.mee.manage.vo.ugg.OrderListResult;
import com.mee.manage.vo.ugg.QueryOrder;
import com.mee.manage.vo.ugg.QueryOrderRsp;
import com.mee.manage.vo.ugg.QueryParams;
import com.mee.manage.vo.ugg.UggOrderData;
import com.mee.manage.vo.ugg.UggProductDetail;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api/ugg")
@CrossOrigin
public class UggController extends BaseController {
    
    @Autowired
    IUggService uggService;

    @RequestMapping(value = "/token/{bizId}", method = RequestMethod.POST)
    public MeeResult authToken(@PathVariable("bizId") Long bizId, 
                                @RequestParam("username") String username, 
                                @RequestParam("password") String password) {
        
        MeeResult meeResult = new MeeResult();
        try {
            String token = uggService.authToken(bizId, username, password);
            if(StringUtils.isNotEmpty(token))
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } catch (MeeException ex) {
            meeResult.setStatusCodeDes(ex.getStatusCode());
            logger.error("getToken Error bizId = {}", bizId, ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("getToken Error bizId = {}", bizId, ex);
        }
        return meeResult;
    }

    @RequestMapping(value = "/login/{bizId}", method = RequestMethod.POST)
    public MeeResult login(@PathVariable("bizId") Long bizId, 
                           @RequestParam("username") String username, 
                           @RequestParam("password") String password) {
        MeeResult meeResult = new MeeResult();
        try {
            String token = uggService.login(bizId, username, password);
            if(StringUtils.isNotEmpty(token))
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } catch (MeeException ex) {
            meeResult.setStatusCodeDes(ex.getStatusCode());
            logger.error("getToken Error bizId = {}", bizId, ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("getToken Error bizId = {}", bizId, ex);
        }
        return meeResult;   
    }

    @RequestMapping(value = "/token/{bizId}", method = RequestMethod.GET)
    public MeeResult getToken(@PathVariable("bizId") Long bizId) {

        MeeResult meeResult = new MeeResult();
        try {
            String token = uggService.getToken(bizId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(token);
        } catch (MeeException ex) {
            meeResult.setStatusCodeDes(ex.getStatusCode());
            logger.error("getToken Error bizId = {}", bizId, ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("getToken Error bizId = {}", bizId, ex);
        }
        return meeResult;

    }


    @RequestMapping(value = "/order/create/{bizId}", method = RequestMethod.PUT)
    public MeeResult save(@PathVariable("bizId") Long bizId, @RequestBody UggOrder orders) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean result = uggService.saveUggOrder(orders, bizId);
            meeResult.setStatusCode(result ? StatusCode.SUCCESS.getCode() : StatusCode.FAIL.getCode());
        } catch (MeeException ex) {
            meeResult.setStatusCodeDes(ex.getStatusCode());
            logger.error("Save Ugg Order Error", ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("Save Ugg Order Error", ex);
        }
        return meeResult;
    }


    @RequestMapping(value = "/product/{bizId}/{sku}", method = RequestMethod.GET)
    public MeeResult getDetailBySKU(@PathVariable("bizId") Long bizId, @PathVariable("sku") Long sku) {
        MeeResult meeResult = new MeeResult();
        try {
            UggProductDetail detail = uggService.getDetailBySKU(bizId,sku);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(detail);
        } catch (MeeException ex) {
            meeResult.setStatusCodeDes(ex.getStatusCode());
            logger.error("getToken Error bizId = {}", bizId, ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("getToken Error bizId = {}", bizId, ex);
        }
        return meeResult;
    }


    @RequestMapping(value = "/order/list/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    public MeeResult searchOrders(@PathVariable("pageIndex") Integer pageIndex,
                                  @PathVariable("pageSize") Integer pageSize,
                                  @RequestBody QueryParams params) {

        MeeResult meeResult = new MeeResult();
        try {
            OrderListResult result = uggService.getOrders(params, pageIndex, pageSize);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(result);
        } catch (MeeException ex) {
            meeResult.setStatusCodeDes(ex.getStatusCode());
            logger.error("SearchOrders Error", ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("SearchOrders Error bizId", ex);
        }
        return meeResult;   
    }

    @RequestMapping(value = "/order/count", method = RequestMethod.POST)
    public MeeResult searchCount(@RequestBody QueryParams params) {
        MeeResult meeResult = new MeeResult();
        try {
            List<OrderCountResult> result = uggService.getOrderCount(params);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(result);
        } catch (MeeException ex) {
            meeResult.setStatusCodeDes(ex.getStatusCode());
            logger.error("SearchOrders Error", ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("SearchOrders Error bizId", ex);
        }
        return meeResult;
    }

    @RequestMapping(value = "/order/batch", method = RequestMethod.POST)
    public MeeResult createBatchOrder(@RequestBody List<UggOrder> orders) {
        MeeResult meeResult = new MeeResult();
        try {
            String batchId = uggService.createBatchOrder(orders);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(batchId);
        } catch (MeeException ex) {
            meeResult.setStatusCodeDes(ex.getStatusCode());
            logger.error("SearchOrders Error", ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("SearchOrders Error bizId", ex);
        }
        return meeResult;
    }

    @RequestMapping(value = "/order/df/{bizId}", method = RequestMethod.POST)
    public MeeResult dfOrder(@PathVariable("bizId") Long bizId, @RequestBody List<UggOrder> orders) {
        MeeResult meeResult = new MeeResult();
        try {
            Boolean flag = uggService.sendOrders(orders, bizId);
            meeResult.setStatusCode(flag ? StatusCode.SUCCESS.getCode() : StatusCode.FAIL.getCode());
        } catch (MeeException ex) {
            meeResult.setStatusCodeDes(ex.getStatusCode());
            logger.error("SearchOrders Error", ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("SearchOrders Error bizId", ex);
        }
        return meeResult;
    }

    @RequestMapping(value = "/order/query/{bizId}", method = RequestMethod.POST)
    public MeeResult query(@PathVariable("bizId") Long bizId, @RequestBody QueryOrder params) {
        MeeResult meeResult = new MeeResult();
        try {
            QueryOrderRsp result = uggService.queryUggOrders(params, bizId);
            meeResult.setData(result);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
        } catch (MeeException ex) {
            meeResult.setStatusCodeDes(ex.getStatusCode());
            logger.error("SearchOrders Error", ex);
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("SearchOrders Error bizId", ex);
        }
        return meeResult;
    }
}

package com.mee.manage.controller;

import java.util.List;

import com.mee.manage.exception.MeeException;
import com.mee.manage.po.DataStatistics;
import com.mee.manage.service.IDataStatisticsService;
import com.mee.manage.service.IOrderService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.DataTotal;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.OrderStatisticsData;
import com.mee.manage.vo.YiyunOrderDatePeriod;
import com.mee.manage.vo.Yiyun.YiyunErrorVo;
import com.mee.manage.vo.Yiyun.YiyunNoShipVo;
import com.mee.manage.vo.Yiyun.YiyunOrderSales;
import com.mee.manage.vo.Yiyun.YiyunOrderVo;
import com.mee.manage.vo.Yiyun.YiyunTodayData;

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
@RequestMapping("/api")
@CrossOrigin
public class OrderController extends BaseController {

    @Autowired
    IOrderService orderService;

    @Autowired
    IDataStatisticsService dataStatisticsService;

    @RequestMapping(value = "/order/{bizId}", method = RequestMethod.POST)
    public MeeResult queryOrder(@PathVariable("bizId") Integer bizId, @RequestBody YiyunOrderVo orderVo) {
        MeeResult meeResult = new MeeResult();
        try {
            List<YiyunOrderSales> sales = orderService.getYiyunOrder(bizId, orderVo);
            meeResult.setData(sales);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
        } catch (MeeException ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCodeDes(ex.getStatusCode());
        } catch (Exception ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/today/{bizId}", method = RequestMethod.GET)
    public MeeResult todayData(@PathVariable("bizId") Integer bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            YiyunTodayData sales = orderService.getTodayData(bizId);
            meeResult.setData(sales);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
        } catch (MeeException ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCodeDes(ex.getStatusCode());
        } catch (Exception ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/total/{bizId}", method = RequestMethod.GET)
    public MeeResult totalData(@PathVariable("bizId") Integer bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            DataTotal totalData = orderService.getTotalData(bizId);
            meeResult.setData(totalData);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
        } catch (MeeException ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCodeDes(ex.getStatusCode());
        } catch (Exception ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/noshipped/{bizId}", method = RequestMethod.GET)
    public MeeResult noshipped(@PathVariable("bizId") Integer bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            YiyunNoShipVo data = orderService.getNoShipData(bizId);
            meeResult.setData(data);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
        } catch (MeeException ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCodeDes(ex.getStatusCode());
        } catch (Exception ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }
    
    @RequestMapping(value = "/errororder/{bizId}", method = RequestMethod.GET)
    public MeeResult errorOrder(@PathVariable("bizId") Integer bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            YiyunErrorVo data = orderService.getErrorData(bizId);
            meeResult.setData(data);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
        } catch (MeeException ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCodeDes(ex.getStatusCode());
        } catch (Exception ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/datastatic/{bizId}", method = RequestMethod.GET)
    public MeeResult getDataStatic(@PathVariable("bizId") Integer bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            DataStatistics data = dataStatisticsService.getErrorOrder(bizId);
            meeResult.setData(data);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
        } catch (MeeException ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCodeDes(ex.getStatusCode());
        } catch (Exception ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }


 @RequestMapping(value = "/refreshdatastatic/{bizId}", method = RequestMethod.PUT)
    public MeeResult refreshDataStatic(@PathVariable("bizId") Integer bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean data = dataStatisticsService.saveStaticOrder(bizId);
            if(data) {
                meeResult.setStatusCodeDes(StatusCode.SUCCESS);
            } else {
                meeResult.setStatusCodeDes(StatusCode.FAIL);
            }
        } catch (MeeException ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCodeDes(ex.getStatusCode());
        } catch (Exception ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/statistics/{bizId}", method = RequestMethod.POST)
    public MeeResult orderData(@PathVariable("bizId") Integer bizId, @RequestBody YiyunOrderDatePeriod orderVo) {
        MeeResult meeResult = new MeeResult();
        try {
            List<OrderStatisticsData> data = orderService.getStatistionDatas(bizId, orderVo);
            meeResult.setData(data);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
        } catch (MeeException ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCodeDes(ex.getStatusCode());
        } catch (Exception ex) {
            logger.error("queryOrder Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

}
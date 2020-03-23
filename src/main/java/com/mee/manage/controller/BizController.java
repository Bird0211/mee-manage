package com.mee.manage.controller;
import com.mee.manage.po.Biz;
import com.mee.manage.service.IBizService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * BizController
 */
@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class BizController extends BaseController {

    @Autowired
    IBizService bizService;

    @RequestMapping(value = "/allbiz/{bizid}", method = RequestMethod.GET)
    public MeeResult getBiz(@PathVariable("bizid") String bizId) {

        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            if(bizId.equals("all"))
                meeResult.setData(bizService.getAllBiz());
            else
                meeResult.setData(bizService.getBiz(Integer.parseInt(bizId)));

        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("getAllBiz Error bizId = {}", bizId,ex);
        }
        return meeResult;
    }

    @RequestMapping(value = "/biz/add", method = RequestMethod.POST)
    public MeeResult addBiz(@RequestBody Biz biz) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(bizService.save(biz));

        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/biz/update", method = RequestMethod.POST)
    public MeeResult updateBiz(@RequestBody Biz biz) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(bizService.updateById(biz));

        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }
    
}
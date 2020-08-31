package com.mee.manage.controller;

import java.util.List;

import com.mee.manage.exception.MeeException;
import com.mee.manage.po.NzpostConfig;
import com.mee.manage.service.INzpostConfigService;
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

@Controller
@ResponseBody
@RequestMapping("/api/nzpostconfig")
@CrossOrigin
public class NzpostConfigController extends BaseController {

    @Autowired
    INzpostConfigService nzPostConfigService;
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public MeeResult addNzpostConfig(@RequestBody NzpostConfig nzpostConfig) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = nzPostConfigService.save(nzpostConfig);
            meeResult.setStatusCode(flag ? StatusCode.SUCCESS.getCode() : StatusCode.FAIL.getCode());

        } catch (MeeException meeEx) {
            logger.error("requestToken Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("requestToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public MeeResult updateNzpostConfig(@RequestBody NzpostConfig nzpostConfig) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = nzPostConfigService.updateById(nzpostConfig);
            meeResult.setStatusCode(flag ? StatusCode.SUCCESS.getCode() : StatusCode.FAIL.getCode());

        } catch (MeeException meeEx) {
            logger.error("requestToken Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("requestToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public MeeResult deleteNzpostConfig(@PathVariable("id") Long id) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = nzPostConfigService.removeById(id);
            meeResult.setStatusCode(flag ? StatusCode.SUCCESS.getCode() : StatusCode.FAIL.getCode());

        } catch (MeeException meeEx) {
            logger.error("requestToken Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("requestToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    }

    @RequestMapping(value = "/list/{bizId}", method = RequestMethod.GET)
    public MeeResult getNzpostConfig(@PathVariable("bizId") Long bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            List<NzpostConfig> result = nzPostConfigService.getNzpostConfigs(bizId);
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


}
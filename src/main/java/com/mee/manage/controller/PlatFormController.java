package com.mee.manage.controller;

import java.util.List;

import com.mee.manage.po.PlatformConfig;
import com.mee.manage.service.IPlatformConfigService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.PlatFormBaseInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * PlatFormController
 */

@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class PlatFormController extends BaseController {

    @Autowired
    IPlatformConfigService platFormService;

    @RequestMapping(value = "/platform/{bizId}/{platcode}", method = RequestMethod.GET)
    public MeeResult getPlatForm(@PathVariable("bizId") Long bizId, @PathVariable("platcode") String platcode){
        MeeResult meeResult = new MeeResult();
        try {

            List<PlatFormBaseInfo> PlatFormBaseInfo = platFormService.getPlatFormByPlatCode(bizId, platcode);
            meeResult.setData(PlatFormBaseInfo);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getUserMenu Error");
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    };

    @RequestMapping(value = "/platform/detail/{bizId}/{platcode}", method = RequestMethod.GET)
    public MeeResult getPlatFormDetail(@PathVariable("bizId") Long bizId, @PathVariable("platcode") String platcode){
        MeeResult meeResult = new MeeResult();
        try {

            List<PlatformConfig> PlatFormBaseInfo = platFormService.getPlatForm(bizId, platcode);
            meeResult.setData(PlatFormBaseInfo);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getUserMenu Error");
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    };

    @RequestMapping(value = "/platform/del/{platformId}", method = RequestMethod.DELETE)
    public MeeResult delPlatFor(@PathVariable("platformId") Long platformId) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = platFormService.removeById(platformId);
            meeResult.setStatusCode(flag?StatusCode.SUCCESS.getCode():StatusCode.FAIL.getCode());
        } catch (Exception ex) {
            logger.error("getUserMenu Error");
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }
    
}
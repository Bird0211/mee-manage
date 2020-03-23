package com.mee.manage.controller;

import com.mee.manage.po.BizMenu;
import com.mee.manage.service.IBizMenuService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.EditBizMenuVo;
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
 * BizMenuController
 */
@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class BizMenuController extends BaseController {

    @Autowired
    IBizMenuService bizMenuService;

    @RequestMapping(value = "/bizmenu/{bizid}", method = RequestMethod.GET)
    public MeeResult getBizMenu(@PathVariable("bizid") Long bizId) {

        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(bizMenuService.getBizMenuByBizId(bizId));

        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("getBizMenu Error bizId = {}", bizId,ex);
        }
        return meeResult;
    }

    @RequestMapping(value = "/bizmenu/add", method = RequestMethod.POST)
    public MeeResult addBizMenu(@RequestBody BizMenu bizMenu) {

        MeeResult meeResult = new MeeResult();
        try {
            BizMenu bMenu = bizMenuService.addBizMenu(bizMenu);
            if(bMenu != null) {
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            } else {
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
            }
           
            meeResult.setData(bMenu);

        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("addBizMenu Error ",ex);
        }
        return meeResult;
    }

    @RequestMapping(value = "/bizmenu/del/{bizmenuId}", method = RequestMethod.DELETE)
    public MeeResult delBizMenu(@PathVariable("bizmenuId") Long bizmenuId) {

        MeeResult meeResult = new MeeResult();
        try {
            boolean result = bizMenuService.removeBizMenu(bizmenuId);
            if(result) {
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            } else {
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
            }

        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("addBizMenu Error ",ex);
        }
        return meeResult;
    }


    @RequestMapping(value = "/bizmenu/edit", method = RequestMethod.POST)
    public MeeResult editBizMenu(@RequestBody EditBizMenuVo bizMenuVo) {

        MeeResult meeResult = new MeeResult();
        try {
            boolean result = bizMenuService.updateBizMenu(bizMenuVo);
            if(result) {
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            } else {
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
            }

        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
            logger.error("addBizMenu Error ",ex);
        }
        return meeResult;
    }

    
}
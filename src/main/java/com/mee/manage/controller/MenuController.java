package com.mee.manage.controller;

import com.mee.manage.po.Menu;
import com.mee.manage.service.IMenuService;
import com.mee.manage.service.IUserMenuService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class MenuController extends BaseController {

    @Autowired
    IUserMenuService userMenuService;

    @Autowired
    IMenuService menuService;

    @RequestMapping(value = "/menu/{bizId}/{userId}", method = RequestMethod.GET)
    public MeeResult getMenuByUser(@PathVariable("bizId") Long bizId, @PathVariable("userId") Long userId) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(userMenuService.getMenuByUserId(userId,bizId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getUserMenu Error userId = {}", userId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    };

    @RequestMapping(value = "/allmenu/{bizid}", method = RequestMethod.GET)
    public MeeResult getAllMenu(@PathVariable("bizid") String bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(menuService.getAllMenu(bizId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
        } catch (Exception ex) {
            logger.error("getAllMenu Error ", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/menu/add", method = RequestMethod.POST)
    public MeeResult addMenu(@RequestBody Menu menu) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(menuService.insertMenu(menu));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getAllMenu Error ", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/menu/update", method = RequestMethod.POST)
    public MeeResult updateMenu(@RequestBody Menu menu) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(menuService.updateMenu(menu));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getAllMenu Error ", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/menu/flow/{bizId}", method = RequestMethod.GET)
    public MeeResult getTopMemu(@PathVariable("bizId") Long bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(menuService.getOrderFlowMenu(bizId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getAllMenu Error ", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/menu/sub/{bizId}/{userId}/{menuId}", method = RequestMethod.GET)
    public MeeResult getSubMenu(@PathVariable("bizId") Long bizId,
                                @PathVariable("userId") Long userId,
                                @PathVariable("menuId") Long menuId) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(userMenuService.getMenuByUserId(userId, bizId, menuId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getAllMenu Error ", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }
}

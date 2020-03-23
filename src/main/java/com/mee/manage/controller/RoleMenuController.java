package com.mee.manage.controller;

import com.mee.manage.po.RoleMenu;
import com.mee.manage.service.IRoleMenuService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.RoleMenusVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MenuRoleController
 */
@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class RoleMenuController extends BaseController {

    @Autowired
    IRoleMenuService roleMenuService;

    @RequestMapping(value = "/role/menuid/{roleId}", method = RequestMethod.GET)
    public MeeResult getMenuIdByRoleId(@PathVariable("roleId") Long roleId){
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleMenuService.getRoleMenuByRoleId(roleId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getUserMenu Error roleId = {}", roleId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    };

    @RequestMapping(value = "/role/menu/{roleId}", method = RequestMethod.GET)
    public MeeResult getMenuByRoleId(@PathVariable("roleId") Long roleId) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleMenuService.getRoleMenuByRoleId(roleId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getUserMenu Error roleId = {}", roleId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    };

    @RequestMapping(value = "/role/menu/add", method = RequestMethod.POST)
    public MeeResult addRoleMenu(@RequestBody RoleMenu roleMenu) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleMenuService.addRoleMenu(roleMenu));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getUserMenu Error ", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/role/menu/del/{roleId}", method = RequestMethod.DELETE)
    public MeeResult delRoleMenu(@PathVariable("roleId") Long roleId) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleMenuService.delRole(roleId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getUserMenu Error ", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/role/menu/delid/{roleMenuId}", method = RequestMethod.DELETE)
    public MeeResult delRoleMenuId(@PathVariable("roleMenuId") Long roleMenuId) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleMenuService.delRoleMenu(roleMenuId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getUserMenu Error ", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/role/menu/update", method = RequestMethod.POST)
    public MeeResult updateRoleMenuId(@RequestBody RoleMenusVo roleMenus) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = roleMenuService.updateRoleMenus(roleMenus);
            if(flag) {
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            }else {
                meeResult.setStatusCode(StatusCode.FAIL.getCode());

            }
            meeResult.setData(flag);

        } catch (Exception ex) {
            logger.error("getUserMenu Error ", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }
}
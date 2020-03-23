package com.mee.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mee.manage.service.IRoleService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.RoleVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RoleController
 */
@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class RoleController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    IRoleService roleService;


    @RequestMapping(value = "/role/query/{bizId}", method = RequestMethod.GET)
    public MeeResult getRole(@PathVariable("bizId") Long bizId){
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleService.getRoleByBiz(bizId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
        } catch (Exception ex) {
            logger.error("getRole Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    };


    @RequestMapping(value = "/role/add", method = RequestMethod.POST)
    public MeeResult addRole(@RequestBody RoleVo roleVo) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleService.addRole(roleVo));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
        } catch (Exception ex) {
            logger.error("getRole Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    
    @RequestMapping(value = "/role/edit", method = RequestMethod.POST)
    public MeeResult editRole(@RequestBody RoleVo roleVo) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleService.editRole(roleVo.toRole()));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
        } catch (Exception ex) {
            logger.error("getRole Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/role/del/{roleId}", method = RequestMethod.DELETE)
    public MeeResult delRole(@PathVariable("roleId") Long roleId) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleService.delRole(roleId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
        } catch (Exception ex) {
            logger.error("getRole Error bizId = {}", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

}
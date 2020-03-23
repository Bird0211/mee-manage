package com.mee.manage.controller;

import com.mee.manage.service.IRoleUserService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.RoleUserVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * RoleUserController
 */
@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class RoleUserController extends BaseController{

    @Autowired
    IRoleUserService roleUserService;
    
    @RequestMapping(value = "/roleuser/role/{roleId}", method = RequestMethod.GET)
    public MeeResult getRoleUserByRoleId(@PathVariable("roleId") Long roleId){
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleUserService.getRoleUseByRoleId(roleId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getRoleUserByRoleId Error roleId = {}", roleId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    };
    
    @RequestMapping(value = "/roleuser/user/{userId}", method = RequestMethod.GET)
    public MeeResult getRoleUserByUserId(@PathVariable("userId") Long userId){
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleUserService.getRoleUserByUserId(userId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getRoleUserByUserId Error userId = {}", userId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    };

    @RequestMapping(value = "/roleuser/del/role/{roleId}", method = RequestMethod.DELETE)
    public MeeResult delRoleUserByRole(@PathVariable("roleId") Long roleId){
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleUserService.removeRoleUserByRoleId(roleId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("delRoleUserByRole Error roleId = {}", roleId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    };

    @RequestMapping(value = "/roleuser/del/roleid/{id}", method = RequestMethod.DELETE)
    public MeeResult delRoleUserById(@PathVariable("id") Long id){
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleUserService.removeRoleUserById(id));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("delRoleUserByRole Error Id = {}", id, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    };

    @RequestMapping(value = "/roleuser/update", method = RequestMethod.POST)
    public MeeResult updateRoleUser(@RequestBody RoleUserVo roleUserVo) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(roleUserService.updateRoleUser(roleUserVo));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("delRoleUserByRole Error ", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }
    
}
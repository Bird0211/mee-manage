package com.mee.manage.controller;


import java.util.List;

import com.alibaba.fastjson.JSON;
import com.mee.manage.po.Fee;
import com.mee.manage.po.User;
import com.mee.manage.service.IAuthenticationService;
import com.mee.manage.service.IFeeService;
import com.mee.manage.service.ISettleService;
import com.mee.manage.service.ISpecialSkuService;
import com.mee.manage.service.IUserService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.AuthenticationVo;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.SettleFeeVo;
import com.mee.manage.vo.SettleVo;
import com.mee.manage.vo.Yiyun.YiyunUserData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/api")
@CrossOrigin
public class ManageController {

    private static final Logger logger = LoggerFactory.getLogger(ManageController.class);

    @Autowired
    IUserService userService;

    @Autowired
    IFeeService feeService;

    @Autowired
    ISpecialSkuService specialSkuService;

    @Autowired
    ISettleService settleService;

    @Autowired
    IAuthenticationService authService;

    @RequestMapping(value = "/getuser", method = RequestMethod.POST)
    public MeeResult queryUser(@RequestParam("name") String name) {
        MeeResult meeResult = new MeeResult();
        try {
            User user = userService.getUserByName(name);
            if (user != null) {
                meeResult.setData(user);
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            } else
                meeResult.setStatusCode(StatusCode.USER_NOT_EXIST.getCode());
        } catch (Exception ex) {
            logger.error("getUser Error name = {}", name, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }


    @RequestMapping(value = "/getfee", method = RequestMethod.POST)
    public MeeResult queryFee(@RequestParam("userId") Long userId) {
        MeeResult meeResult = new MeeResult();
        try {
            User user = null;
            if (userId != null) {
                user = userService.getById(userId);
            }

            int userType = 1;
            if (user == null) {
                logger.info("user is not exist,set default userType = 1, userId = {}", userId);
            } else
                userType = user.getType();

            List<Fee> list = feeService.getFeeList(userType);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(list);

        } catch (Exception ex) {
            logger.error("getfee Error userId = {}", userId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/settle",method = RequestMethod.POST)
    public MeeResult settlement(@RequestBody SettleVo settleVo){
        logger.info("Params: = {}", JSON.toJSONString(settleVo));

        MeeResult meeResult = new MeeResult();
        try {
            if(settleService.checkParams(settleVo)){

                List<SettleFeeVo> settleFee = settleService.getSettleFee(settleVo);
                if(settleFee != null) {
                    meeResult.setData(settleFee);
                    meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
                }

            }else {
                meeResult.setStatusCode(StatusCode.PARAM_ERROR.getCode());
            }

        } catch (Exception ex) {
            logger.error("settlement error",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }


        return meeResult;
    }

    @RequestMapping(value = "/authentication",method = RequestMethod.POST)
    public MeeResult authentication(@RequestBody AuthenticationVo auth) {
        logger.info("Params: = {}", JSON.toJSONString(auth));
        MeeResult meeResult = new MeeResult();
        try {
            if(authService.checkAuth(auth))
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());

        } catch (Exception ex) {
            logger.error("authentication error",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/user/{bizId}/{userId}",method = RequestMethod.GET)
    public MeeResult queryUserById(@PathVariable String bizId,@PathVariable String userId){
        MeeResult meeResult = new MeeResult();
        try {
            YiyunUserData userData = userService.getYiyunUser(bizId,userId);
            if(userData != null) {
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
                meeResult.setData(userData);
            } else {
                meeResult.setStatusCode(StatusCode.USER_NOT_EXIST.getCode());
            }
        } catch (Exception ex) {
            logger.error("authentication error",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/alluser/{bizId}",method = RequestMethod.GET)
    public MeeResult queryAllUser(@PathVariable String bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            List<YiyunUserData> userData = userService.getAllYiYunUser(bizId);
            if(userData != null) {
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
                meeResult.setData(userData);
            } else {
                meeResult.setStatusCode(StatusCode.USER_NOT_EXIST.getCode());
            }
        } catch (Exception ex) {
            logger.error("authentication error",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

}

package com.mee.manage.controller;

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

    @RequestMapping(value = "/menu", method = RequestMethod.POST)
    public MeeResult getMenuByUser(@RequestParam("userId") Long userId){
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(userMenuService.getMenuByUserId(userId));
        } catch (Exception ex) {
            logger.error("getUserMenu Error userId = {}", userId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;

    };


}

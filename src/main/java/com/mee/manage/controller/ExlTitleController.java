package com.mee.manage.controller;

import com.mee.manage.service.IExlTitleService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.ExlTitleVo;
import com.mee.manage.vo.MeeResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class ExlTitleController {

    @Autowired
    IExlTitleService exlTitleService;

    @RequestMapping(value = "/exltitle/query",method = RequestMethod.POST)
    public MeeResult getExlTitle(@RequestParam("name") String name){
        MeeResult meeResult = new MeeResult();
        try {
            ExlTitleVo exlTitleVo = exlTitleService.getExlTitleByName(name);
            meeResult.setData(exlTitleVo);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/exltitle/add",method = RequestMethod.POST)
    public MeeResult addExlTitle(@RequestBody ExlTitleVo exlTitle){
        MeeResult meeResult = new MeeResult();
        try {
            boolean result = exlTitleService.addExlTitle(exlTitle);
            if(result)
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());


        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/exltitle/update",method = RequestMethod.POST)
    public MeeResult updateExlTitle(@RequestBody ExlTitleVo exlTitle){
        MeeResult meeResult = new MeeResult();
        try {
            boolean result = exlTitleService.updateExlTitle(exlTitle);
            if(result)
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());


        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }


}

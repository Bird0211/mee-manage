package com.mee.manage.controller;


import com.mee.manage.service.GuavaCache;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping("/api/guava")
@CrossOrigin
public class GuavaCacheController extends BaseController {

    @Autowired
    GuavaCache guavaCache;

    @RequestMapping(value = "/refresh/{bizId}", method = RequestMethod.POST)
    public MeeResult refresh(@PathVariable("bizId") String bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            guavaCache.refreshCache(bizId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
        } catch (Exception ex) {
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

}

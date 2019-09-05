package com.mee.manage.controller;

import com.mee.manage.vo.MeeResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class TradeMeController {

    @RequestMapping(value = "/requestToken", method = RequestMethod.POST)
    public MeeResult requestToken(String token,String verifier){

        return null;
    }

    @RequestMapping(value = "/checkToken", method = RequestMethod.GET)
    public MeeResult checkToken(){


        return null;
    }


}

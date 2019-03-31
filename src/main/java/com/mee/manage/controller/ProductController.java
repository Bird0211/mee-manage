package com.mee.manage.controller;

import com.mee.manage.po.User;
import com.mee.manage.service.IProductsService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.BathProVos;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.ProVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    IProductsService productsService;

    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    public MeeResult addProduct(@RequestBody ProVo proVo) {
        MeeResult meeResult = new MeeResult();
        try {

            boolean result = productsService.insertProduct(proVo);
            if(result)
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());

        } catch (Exception ex) {
            logger.info("addProduct error",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/addBathProduct", method = RequestMethod.POST)
    public MeeResult addBathProducts(@RequestBody BathProVos proVos){
        MeeResult meeResult = new MeeResult();
        try {

            boolean result = productsService.insertProducts(proVos);
            if(result)
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());

        } catch (Exception ex) {
            logger.info("addProduct error",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

}

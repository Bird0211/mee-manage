package com.mee.manage.controller;

import com.google.common.collect.Lists;
import com.mee.manage.po.User;
import com.mee.manage.service.IProductsService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping(value = "/allProducts", method = RequestMethod.POST)
    public MeeResult getAllProducts(@RequestParam("bizId") String bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            List<MeeProductVo> result = productsService.getMeeProducts(bizId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(result);

        } catch (Exception ex) {
            logger.info("addProduct error",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/allSuppliers", method = RequestMethod.POST)
    public MeeResult getAllSuppliers(@RequestParam("bizId") String bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            List<SuppliersVo> result = productsService.getSuppliers(bizId);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(result);
        } catch (Exception ex) {
            logger.info("addProduct error",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/getEmptyWeight", method = RequestMethod.GET)
    public MeeResult getEmptyWeight(){
        MeeResult meeResult = new MeeResult();
        try {
            List<MeeProductVo> result = productsService.getMeeProducts("20");
            List<MeeProductVo> emptyWeight = Lists.newArrayList();
            for(MeeProductVo product : result){
                if (product.getWeight() == null ||
                product.getWeight().equals("") ||
                product.getWeight().equals("0"))
                    emptyWeight.add(product);
            }

            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            meeResult.setData(emptyWeight);

        } catch (Exception ex) {
            logger.info("addProduct error",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

}

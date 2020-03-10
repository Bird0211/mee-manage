package com.mee.manage.controller;


import com.mee.manage.service.IYmtouService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.*;
import com.mee.manage.vo.ymt.YmtGoodInfo;
import com.mee.manage.vo.ymt.YmtouOrderListParam;
import com.mee.manage.vo.ymt.YmtouOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class YmtouController extends BaseController{

    @Autowired
    IYmtouService ymtouService;

    @RequestMapping(value = "/ymtou/order", method = RequestMethod.POST)
    public MeeResult getOrderList(@RequestBody YmtouOrderListParam param){
        MeeResult meeResult = new MeeResult();
        try {
            YmtouOrderVo ymtouOrderList = ymtouService.getOrderList(param);
            meeResult.setData(ymtouOrderList);
        } catch (Exception ex) {
            logger.error("checkToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/ymtou/product/list", method = RequestMethod.GET)
    public MeeResult getProductList() {
        MeeResult meeResult = new MeeResult();
        try {
            List<YmtGoodInfo> ymtouProductList = ymtouService.getProdudcts();
            meeResult.setData(ymtouProductList);
        } catch (Exception ex) {
            logger.error("checkToken Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

}

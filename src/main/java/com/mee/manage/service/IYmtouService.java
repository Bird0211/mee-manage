package com.mee.manage.service;


import com.mee.manage.vo.ymt.YmtGoodInfo;
import com.mee.manage.vo.ymt.YmtouOrderListParam;
import com.mee.manage.vo.ymt.YmtouOrderVo;
import com.mee.manage.vo.ymt.YmtouProduct;

import java.util.List;

public interface IYmtouService {

    YmtouOrderVo getOrderList(YmtouOrderListParam params);

    List<YmtouProduct> getProductList();

    List<YmtGoodInfo> getProdudcts();

}

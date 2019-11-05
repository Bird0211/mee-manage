package com.mee.manage.service;


import com.mee.manage.vo.*;

import java.util.List;

public interface IYmtouService {

    YmtouOrderVo getOrderList(YmtouOrderListParam params);

    List<YmtouProduct> getProductList();

    List<YmtGoodInfo> getProdudcts();

}

package com.mee.manage.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.UggOrder;
import com.mee.manage.vo.ugg.OrderCountResult;
import com.mee.manage.vo.ugg.QueryParams;

public interface IUggOrderService extends IService<UggOrder> {
    
    IPage<UggOrder> getOrdersByPage(Integer pageIndex, Integer pageSize, QueryParams params);

    List<OrderCountResult> getOrderCount(QueryParams params);

    List<UggOrder> getPreDeliveryOrder(List<String> orderIds);

}

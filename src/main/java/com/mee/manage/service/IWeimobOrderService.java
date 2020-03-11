package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.WeimobOrder;


public interface IWeimobOrderService extends IService<WeimobOrder> {

    WeimobOrder getWeimboOrders(Long sku);

    WeimobOrder getWeimobOrder(Long sku,Long goodId);

    boolean addWeimobOrder(WeimobOrder weimobOrder);

    boolean updateWeimobOrder(WeimobOrder weimobOrder);

}

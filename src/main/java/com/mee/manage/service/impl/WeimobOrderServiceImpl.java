package com.mee.manage.service.impl;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IWeimobOrderMapper;
import com.mee.manage.po.WeimobOrder;
import com.mee.manage.service.IWeimobOrderService;

import org.springframework.stereotype.Service;

@Service
public class WeimobOrderServiceImpl extends ServiceImpl<IWeimobOrderMapper, WeimobOrder> implements IWeimobOrderService {


    @Override
    public WeimobOrder getWeimboOrders(Long sku) {
        if(sku == null || sku <= 0)
            return null;

        QueryWrapper<WeimobOrder> queryWrapper = new QueryWrapper<WeimobOrder>();
        queryWrapper.eq("sku",sku);
        return getOne(queryWrapper);
    }

    @Override
    public WeimobOrder getWeimobOrder(Long sku, Long goodId) {
        if(sku == null || sku <= 0)
            return null;

        QueryWrapper<WeimobOrder> queryWrapper = new QueryWrapper<WeimobOrder>();
        queryWrapper.eq("sku",sku);
        queryWrapper.eq("good_id",goodId);
        return getOne(queryWrapper);
    }

    @Override
    public boolean addWeimobOrder(WeimobOrder weimobOrder) {
        if(weimobOrder == null ||
            weimobOrder.getSku() == null ||
            weimobOrder.getSku() == 0 ||
            weimobOrder.getGoodId() == null ||
            weimobOrder.getGoodId() == 0)
            return false;
        return save(weimobOrder);
    }

    @Override
    public boolean updateWeimobOrder(WeimobOrder weimobOrder) {
        if(weimobOrder == null ||
                weimobOrder.getSku() == null ||
                weimobOrder.getSku() == 0 ||
                (weimobOrder.getLastSalesPrice() == null && weimobOrder.getLastCostPrice() == null))
            return false;


        UpdateWrapper<WeimobOrder> updateWrapper = new UpdateWrapper<WeimobOrder>();
        updateWrapper.eq("sku",weimobOrder.getSku());
        if(weimobOrder.getLastCostPrice() != null && weimobOrder.getLastCostPrice().compareTo(BigDecimal.ZERO) > 0)
            updateWrapper.set("last_cost_price",weimobOrder.getLastCostPrice());

        if(weimobOrder.getLastSalesPrice() != null && weimobOrder.getLastSalesPrice().compareTo(BigDecimal.ZERO) > 0)
            updateWrapper.set("last_sales_price",weimobOrder.getLastSalesPrice());
        return update(updateWrapper);
    }
}

package com.mee.manage.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mee.manage.enums.UggOrderStatusEmun;
import com.mee.manage.mapper.IUggOrderMapper;
import com.mee.manage.po.UggOrder;
import com.mee.manage.service.IUggOrderService;
import com.mee.manage.vo.ugg.OrderCountResult;
import com.mee.manage.vo.ugg.QueryParams;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class UggOrderServiceImpl extends ServiceImpl<IUggOrderMapper, UggOrder> implements IUggOrderService {

    @Override
    public IPage<UggOrder> getOrdersByPage(Integer pageIndex, Integer pageSize, QueryParams params) {
        Page<UggOrder> ipage = new Page<>(pageIndex, pageSize);
        QueryWrapper<UggOrder> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(params.getExtId())) {
            queryWrapper.eq("ext_id", params.getExtId());
        }

        if (StringUtils.isNotEmpty(params.getBatchId())) {
            queryWrapper.eq("batch_id", params.getBatchId());
        }

        if (params.getBizId() != null && params.getBizId() > 0) {
            queryWrapper.eq("biz_id", params.getBizId());
        }

        if (params.getStart() != null && params.getEnd() != null) {
            queryWrapper.between("create_time", params.getStart(), params.getEnd());
        }

        if (StringUtils.isNotEmpty(params.getResource())) {
            queryWrapper.eq("resource", params.getResource());
        }

        if (params.getStatus() != null && params.getStatus() > 0) {
            queryWrapper.eq("status", params.getStatus());
        }
        queryWrapper.orderByDesc("id");

        IPage<UggOrder> pageResult = page(ipage, queryWrapper);
        return pageResult;
    }

    @Override
    public List<OrderCountResult> getOrderCount(QueryParams params) {
        QueryWrapper<UggOrder> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(params.getExtId())) {
            queryWrapper.eq("ext_id", params.getExtId());
        }

        if (StringUtils.isNotEmpty(params.getBatchId())) {
            queryWrapper.eq("batch_id", params.getBatchId());
        }

        if (params.getBizId() != null && params.getBizId() > 0) {
            queryWrapper.eq("biz_id", params.getBizId());
        }

        if (params.getStart() != null && params.getEnd() != null) {
            queryWrapper.between("create_time", params.getStart(), params.getEnd());
        }

        if (StringUtils.isNotEmpty(params.getResource())) {
            queryWrapper.eq("resource", params.getResource());
        }

        queryWrapper.select("status", "count(id) as count");
        queryWrapper.groupBy("status");

        List<OrderCountResult> countResults = null;
        List<Map<String, Object>> result = listMaps(queryWrapper);
        if(result != null) {
            countResults = Lists.newArrayList();
            for(Map<String, Object> obj : result) {
                OrderCountResult countResult = new OrderCountResult();
                countResult.setStatus(Integer.parseInt(obj.get("status").toString()));
                countResult.setOrderCount(Long.parseLong(obj.get("count").toString()));
                countResults.add(countResult);
            }

        }
        return countResults;
    }

    @Override
    public List<UggOrder> getPreDeliveryOrder(List<String> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return null;
        } 

        QueryWrapper<UggOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("ext_id", orderIds.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        queryWrapper.eq("status", UggOrderStatusEmun.PreDelivery.getCode());

        return list(queryWrapper);
    }
    
}

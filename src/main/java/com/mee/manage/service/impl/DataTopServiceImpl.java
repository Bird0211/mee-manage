package com.mee.manage.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mee.manage.mapper.IDataTopMapper;
import com.mee.manage.po.DataTop;
import com.mee.manage.service.IDataTopService;
import com.mee.manage.vo.Yiyun.YiyunTopProduct;

import org.springframework.stereotype.Service;

@Service
public class DataTopServiceImpl extends ServiceImpl<IDataTopMapper,DataTop> implements IDataTopService {

	@Override
	public boolean saveDataTop(Integer bizId, List<YiyunTopProduct> datas) {
        if(datas == null)
            return false;

        removeDataTop(bizId);
        List<DataTop> dataTops = Lists.newArrayList();
        for(YiyunTopProduct data: datas) {
            DataTop dTop = new DataTop();
            dTop.setSku(data.getSku());
            dTop.setBizId(bizId);
            dTop.setNumber((int)data.getNumber().doubleValue());

            dataTops.add(dTop);
        }
        
		return saveBatch(dataTops);
    }
    
    private boolean removeDataTop(Integer bizId) {
        QueryWrapper<DataTop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);

        return remove(queryWrapper);
    }

	@Override
	public List<YiyunTopProduct> getTopProduct(Integer bizId) {
        QueryWrapper<DataTop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        List<DataTop> datas = list(queryWrapper);
        List<YiyunTopProduct> yiyunTopProducts = new ArrayList<>();
        if(datas != null) {
            for(DataTop data: datas) {
                YiyunTopProduct yProduct = new YiyunTopProduct();
                yProduct.setSku(data.getSku());
                yProduct.setNumber(Double.valueOf(data.getNumber()));
                yiyunTopProducts.add(yProduct);
            }
        }

		return yiyunTopProducts.stream().sorted(Comparator.comparing(YiyunTopProduct::getNumber).reversed()).collect(Collectors.toList());
	}

    
}
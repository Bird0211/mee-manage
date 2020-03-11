package com.mee.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.ISpecialSkuMapper;
import com.mee.manage.po.SpecialSku;
import com.mee.manage.service.ISpecialSkuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpecialSkuServiceImpl extends ServiceImpl<ISpecialSkuMapper, SpecialSku> implements ISpecialSkuService {
    @Override
    public List<Long> getSpecialSkuByUserId(Long userId,Integer bathId) {
        if(userId == null)
            return null;

        QueryWrapper<SpecialSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("bath",bathId);

        List<Long> skus = null;
        List<SpecialSku> specialSkus = list(queryWrapper);
        if(specialSkus != null && specialSkus.size() > 0){
            skus = new ArrayList<>();
            for (SpecialSku sku:
                 specialSkus) {
                skus.add(sku.getSku());
            }
        }

        return skus;
    }
}

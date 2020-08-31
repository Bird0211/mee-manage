package com.mee.manage.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.INzpostConfigMapper;
import com.mee.manage.po.NzpostConfig;
import com.mee.manage.service.INzpostConfigService;

import org.springframework.stereotype.Service;

@Service
public class NzpostConfigServiceImpl extends ServiceImpl<INzpostConfigMapper, NzpostConfig>
        implements INzpostConfigService {


    @Override
    public List<NzpostConfig> getNzpostConfigs(Long bizId) {
        QueryWrapper<NzpostConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        
        return list(queryWrapper);
    }


    
}
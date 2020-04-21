package com.mee.manage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IPlatformConfigMapper;
import com.mee.manage.po.PlatformConfig;
import com.mee.manage.service.IPlatformConfigService;
import com.mee.manage.vo.PlatFormBaseInfo;

import org.springframework.stereotype.Service;

/**
 * PlatformConfigServiceImpl
 */
@Service
public class PlatformConfigServiceImpl extends ServiceImpl<IPlatformConfigMapper, PlatformConfig>
        implements IPlatformConfigService {

    @Override
    public PlatformConfig getPlatFormById(Integer id) {
        
        return getById(id);
    }

    @Override
    public List<PlatFormBaseInfo> getPlatFormByPlatCode(Long bizId, String platformCode) {
        QueryWrapper<PlatformConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.eq("platform_code",platformCode);

        List<PlatformConfig> platformConfigs = list(queryWrapper);
        if(platformCode == null) {
            return null;
        }

        List<PlatFormBaseInfo> platFormInfo = platformConfigs.stream().
            map(item -> new PlatFormBaseInfo(item)).collect(Collectors.toList());
        return platFormInfo;
    }

    
}
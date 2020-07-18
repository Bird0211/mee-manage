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
        
        List<PlatformConfig> platformConfigs = getPlatForm(bizId,platformCode);
        if(platformConfigs == null)
            return null;
        
        List<PlatFormBaseInfo> platFormInfo = platformConfigs.stream().
            map(item -> new PlatFormBaseInfo(item)).collect(Collectors.toList());
        return platFormInfo;
    }

    @Override
    public boolean updatePlatFormCode(PlatformConfig platformConfig) {
        
        List<PlatFormBaseInfo> platForms = 
            getPlatFormByPlatCode(platformConfig.getBizId(), platformConfig.getPlatformCode());

        boolean flag = false;
        if(platForms == null || platForms.size() <= 0 || 
                platForms.stream().filter(item -> item.getName().equals(platformConfig.getName())).count() <= 0) {
            flag = save(platformConfig);
        } else {
            List<PlatFormBaseInfo> formInfos = 
                platForms.stream().filter(item -> item.getName().equals(platformConfig.getName())).collect(Collectors.toList());

            PlatFormBaseInfo formInfo = formInfos.get(0);
            platformConfig.setId(formInfo.getId());
            
            flag = updateById(platformConfig);
        }

        return flag;
    }

    @Override
    public List<PlatformConfig> getPlatForm(Long bizId, String platformCode) {
        QueryWrapper<PlatformConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.eq("platform_code",platformCode);

        List<PlatformConfig> platformConfigs = list(queryWrapper);

        return platformConfigs;
    }

    @Override
    public PlatformConfig getOnePlatForm(Long bizId, String platformCode) {
        QueryWrapper<PlatformConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.eq("platform_code",platformCode);
        queryWrapper.last("LIMIT 1");
        return getOne(queryWrapper);
    }

    @Override
    public PlatformConfig getPlatFormByToken(Long bizId, String platformCode, String token) {
        QueryWrapper<PlatformConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.eq("platform_code",platformCode);
        queryWrapper.eq("token", token);
        queryWrapper.last("LIMIT 1");
        return getOne(queryWrapper);
    }

    
}
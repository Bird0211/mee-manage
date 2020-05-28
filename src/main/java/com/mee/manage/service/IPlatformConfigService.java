package com.mee.manage.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.PlatformConfig;
import com.mee.manage.vo.PlatFormBaseInfo;

/**
 * IPlatformConfigService
 */
public interface IPlatformConfigService extends IService<PlatformConfig> {

    PlatformConfig getPlatFormById(Integer id);

    List<PlatFormBaseInfo> getPlatFormByPlatCode(Long bizId, String platformCode);

    List<PlatformConfig> getPlatForm(Long bizId, String platformCode);

    PlatformConfig getOnePlatForm(Long bizId, String platformCode);

    boolean updatePlatFormCode(PlatformConfig platformConfig);
}
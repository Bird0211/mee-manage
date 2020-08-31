package com.mee.manage.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.NzpostConfig;

public interface INzpostConfigService extends IService<NzpostConfig> {

    List<NzpostConfig> getNzpostConfigs(Long bizId);
    

}
package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.Configuration;

import java.util.Date;

public interface IConfigurationService extends IService<Configuration> {

    boolean insertConfig(String key, String value, Date expir);

    boolean updateConfig(String key,String value,Date expir);

    Configuration getConfig(String key);

    String getValue(String key);

    Integer getIntValue(String key);
    
    boolean removeConfig(String key);

}

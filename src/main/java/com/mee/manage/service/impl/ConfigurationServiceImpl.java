package com.mee.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IConfigurationMapper;
import com.mee.manage.po.Configuration;
import com.mee.manage.service.IConfigurationService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ConfigurationServiceImpl extends ServiceImpl<IConfigurationMapper, Configuration>
        implements IConfigurationService {


    @Override
    public boolean insertConfig(String key, String value, Date expir) {
        if(key == null || value == null)
            return false;

        Configuration configuration = new Configuration();
        configuration.setKey(key);
        configuration.setValue(value);
        configuration.setExpir(expir);
        return save(configuration);
    }

    @Override
    public boolean updateConfig(String key, String value, Date expir) {
        if(key == null || value == null)
            return false;

        UpdateWrapper<Configuration> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("value",value);
        updateWrapper.set("expir",expir);
        updateWrapper.eq("`key`",key);

        return update(updateWrapper);
    }

    @Override
    public Configuration getConfig(String key) {
        if(key == null )
            return null;
        QueryWrapper<Configuration> queryWrapper = new QueryWrapper<Configuration>();
        queryWrapper.eq("`key`",key);

        Configuration config = getOne(queryWrapper);
        return config;
    }

    @Override
    public String getValue(String key) {
        Configuration config = getConfig(key);
        if(config == null)
            return null;

        return config.getValue();
    }

    @Override
    public Integer getIntValue(String key) {
        String value = getValue(key);
        if(value == null)
            return null;

        Integer integerValue = null;
        try {
            integerValue = Integer.parseInt(value);
        }catch (Exception e) {

        }
        return integerValue;
    }

    @Override
    public boolean removeConfig(String key) {
        if(key == null )
            return false;

        QueryWrapper<Configuration> queryWrapper = new QueryWrapper<Configuration>();
        queryWrapper.eq("`key`",key);

        return  remove(queryWrapper);
    }


}

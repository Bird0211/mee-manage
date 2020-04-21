package com.mee.manage.vo;

import com.mee.manage.po.PlatformConfig;

import lombok.Data;

/**
 * PlatFormBaseInfo
 */
@Data
public class PlatFormBaseInfo {

    Integer id;

    Long bizId;

    String platformCode;

    String name;


    PlatFormBaseInfo() {

    }

    public PlatFormBaseInfo(PlatformConfig platformConfig) {
        if(platformConfig != null) {
            setId(platformConfig.getId());
            setBizId(platformConfig.getBizId());
            setName(platformConfig.getName());
            setPlatformCode(platformConfig.getPlatformCode());
        }        
    }

    
}
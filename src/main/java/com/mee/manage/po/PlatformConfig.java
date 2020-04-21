package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * PlatformConfig
 */
@Data
@TableName("t_mee_platform_config")
public class PlatformConfig {

    @TableId(type = IdType.AUTO)
    Integer id;

    Long bizId;

    String platformCode;

    String name;

    String token;

    String refreshToken;

    String clientId;

    String clientSecret;
    
}
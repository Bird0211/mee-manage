package com.mee.manage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mee.ymtou")
@Data
public class YmtouConfig {


    private String appId;

    private String appSecret;

    private String authCode;

    private String url;
}

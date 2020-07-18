package com.mee.manage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "mee.trademe")
public class TradeMeConfig {

    String tokenUrl;

    String key;

    String secret;

    String accessTokenUrl;

    String callbackUrl;

    String profileUrl;

    String soltItemUrl;
}
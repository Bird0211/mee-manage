package com.mee.manage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "mee.nzpost")
public class NzPostConfig {

    String clientId;

    String secret;

    String accessTokenUrl;
    
    String createLabelUrl;

    String shippedOptionUrl;
    
    String statusOfLabelUrl;

    String bookingUrl;

    Integer siteCode;

}
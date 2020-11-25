package com.mee.manage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "mee.ugg")
public class UggConfig {
    
    String tokenUrl;

    String detailBySKUUrl;

    String createDFUrl;

    String loginUrl;

    String queryUrl;
}

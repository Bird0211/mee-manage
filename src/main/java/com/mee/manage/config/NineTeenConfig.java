package com.mee.manage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * NineTeenConfig
 */
@Data
@Component
@ConfigurationProperties(prefix = "mee.nineteen")
public class NineTeenConfig {

    String orderListUrl;

    String productUrl;

    String productTypeUrl;

    String productGroupUrl;

    String editProductUrl;
    
    String logisticsUrl;

    String deliveryUrl;

}
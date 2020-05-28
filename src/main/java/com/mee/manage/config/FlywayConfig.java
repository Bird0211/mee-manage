package com.mee.manage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "mee.flyway")
@Data
public class FlywayConfig {

    String tokenUrl;
    String addOrderUrl;
}
package com.mee.manage.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mee")
@Data
public class Config {

    String ocrApiKey;

    String ocrUrl;



}

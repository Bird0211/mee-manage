package com.mee.manage.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mee")
@Data
public class Config {

    public static String WEIMOBTOKEN = "weimobtoken";

    public static String WEIMOBREREFRESHTOKEN = "weimobrefreshtoken";

    public static String PRE_BIZID = "BIZ";

    String ocrApiKey;

    String ocrUrl;

    String bizId;

    String token;

    String allProductUrl;

    String allSupplieUrl;

    String currencyUrl;

    String currencyKey;

}

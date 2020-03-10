package com.mee.manage.config;

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

    public static String OCR_MODE = "OCRMODE";

    public static String PAGE_SEG_MODE = "PAGE_SEG_MODE";

    String ocrApiKey;

    String ocrUrl;

    String allProductUrl;

    String allSupplieUrl;

    String bizUsersUrl;

    String currencyUrl;

    String currencyKey;

    String stockIntake;

    String expressUrl;

    String expreeKey;

    String kdnUserId;

    String kdnKey;

    String kdnNumberIdentifyUrl;

}

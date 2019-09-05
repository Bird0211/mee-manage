package com.mee.manage.util;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mee.weimob")
@Data
public class WeimobConfig {

    String clientId;

    String clientSecret;

    String weimobTokenUrl;

    String returnUri;

    String weimobOrderListUrl;

    String goodsClassifyUrl;

    String goodListUrl;

    String orderDetail;
}

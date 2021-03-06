package com.mee.manage.config;


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

    String goodDetailUrl;

    String updateGoodUrl;

    String storeListUrl;

    String skuProductUrl;

    String batchDeliveryUrl;

    String orderDeliveryUrl;

    String flagOrderUrl;
}

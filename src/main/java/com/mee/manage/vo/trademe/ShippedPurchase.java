package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class ShippedPurchase {
    String orderId;

    String deliveryName;

    String deliveryPhone;

    String deliveryEmail;

    String street;

    String suburb;

    String city;

    String postcode;

    String countryCode;

    String carrier;

    ShippedDimensions[] dimensions;
}
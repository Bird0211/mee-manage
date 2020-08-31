package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class ShippedDimensions {
    Double volumes;

    Double weight;

    Double height;

    Double length;

    Double width;

    String serviceCode;

    String[] addOns;
}
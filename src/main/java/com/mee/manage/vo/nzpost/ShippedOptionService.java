package com.mee.manage.vo.nzpost;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ShippedOptionService {
    
    String carrier;

    String description;

    String service_code;

    BigDecimal price_excluding_gst;

    BigDecimal price_including_gst;

    String estimated_delivery_time;

    String service_standard;

    Boolean tracking_included;

    Boolean signature_included;

    List<OptionServiceAddons> addons;

}
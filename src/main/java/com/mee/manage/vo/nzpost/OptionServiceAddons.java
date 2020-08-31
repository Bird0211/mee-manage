package com.mee.manage.vo.nzpost;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OptionServiceAddons {
    String addon_code;

    String description;

    Boolean mandatory;

    BigDecimal price_excluding_gst;
    
    BigDecimal price_including_gst;
}
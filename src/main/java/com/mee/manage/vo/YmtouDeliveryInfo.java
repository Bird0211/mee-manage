package com.mee.manage.vo;

import lombok.Data;

@Data
public class YmtouDeliveryInfo {
    String logistics_company_code;
    String tracking_number;
    String delivery_time;
    Integer logistics_type;
}

package com.mee.manage.vo.nzpost;

import lombok.Data;

@Data
public class ShippedOptionReq {
    
    String weight;

    String length;

    String width;

    String height;

    String diameter;

    ShippedOpPickup pickup;

    ShippedOpDelivery delivery;

}
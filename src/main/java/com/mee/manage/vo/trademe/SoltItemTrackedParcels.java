package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class SoltItemTrackedParcels {
    
    Integer CourierParcelId;

    String CourierCompanyName;

    String TrackingReference;

    String TrackingUrl;

    Integer Status;

}
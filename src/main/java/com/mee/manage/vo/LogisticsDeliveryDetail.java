package com.mee.manage.vo;

import lombok.Data;

import java.util.Date;

@Data
public class LogisticsDeliveryDetail {

    Integer expectDeliveryType;
    Date expectDeliveryDate;
    Date expectDeliveryStartTime;
    Date expectDeliveryEndTime;
    String expectDeliveryTime;
    String receiverProvince;
    String receiverCity;
    String receiverCounty;
    String receiverArea;
    String receiverAddress;
    String receiverLongitude;
    String receiverLatitude;
    String idCardNo;
    String receiverName;
    String receiverMobile;
    String receiverZip;
}

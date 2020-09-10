package com.mee.manage.vo.nineteen;

import java.util.List;

import lombok.Data;

@Data
public class DeliveryInfo {
    
    Integer orderId;

    List<String> detailId;

    Integer expressId;

    String courierNumber;

}

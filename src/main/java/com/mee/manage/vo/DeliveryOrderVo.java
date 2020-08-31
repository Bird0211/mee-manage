package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeliveryOrderVo {

    String orderId;

    String deliveryId;

    String expressComCode;

    String name;

    String address;

    String phone;

    String id_num;

    List<DeliverySkuInfo> skuInfo;

    boolean split;

}

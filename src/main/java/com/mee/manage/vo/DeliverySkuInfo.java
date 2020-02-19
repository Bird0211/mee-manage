package com.mee.manage.vo;

import lombok.Data;

@Data
public class DeliverySkuInfo {

    String sku;     //商品规格id

    String content;

    Integer skuNum; //发货数量（目前不支持单sku拆分数量）

    Long skuId;

    Long itemId;


}

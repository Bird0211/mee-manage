package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class WeimobSkuVo {

    Long skuId;
    Long goodsId;
    List<String> skuAttrValueNameList;
    String outerSkuCode;
    String imageUrl;
    BigDecimal salePrice;
    BigDecimal originalPrice;
    BigDecimal costPrice;
    String productTitle;
    Map<String,SkuAttrMap> skuAttrMap;


}

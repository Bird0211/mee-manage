package com.mee.manage.vo.ymt;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class YmtGoodInfo {
    String skuId;

    String sku;

    BigDecimal price;
    BigDecimal vipPrice;
    BigDecimal newPrice;

    Integer stockNum;

    Double weight;

    String productId;

    String productName;

    Integer categoryId;

    String categoryName;

    String brandName;

    String productImage;

}

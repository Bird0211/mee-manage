package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodPageList {

    Long goodsId;
    String title;
    BigDecimal maxPrice;
    BigDecimal minPrice;
    Integer avaliableStockNum;
    String defaultImageUrl;
    Integer salesNum;
    java.util.Date putAwayDate;
    Integer isPutAway;
    Integer isMultiSku;
    Integer sortNum;
    Boolean isExistEmptyStock;
    Boolean isAllStockEmpty;
    boolean isCanSell;
}

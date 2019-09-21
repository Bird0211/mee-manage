package com.mee.manage.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GoodDetailVo {

    Long goodsId;

    String title;

    String outerGoodsCode;

    Integer isMultiSku;

    Boolean isDeleted;

    List<String> goodsImageUrl;

    String defaultImageUrl;

    String goodsDesc;

    Integer deductStockType;

    Integer isPutAway;

    Integer sort;

    List<WeimobSkuVo> skuList;

}

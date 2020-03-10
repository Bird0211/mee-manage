package com.mee.manage.vo;

import com.mee.manage.vo.weimob.WeimobSkuVo;
import lombok.Data;

import java.util.List;

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

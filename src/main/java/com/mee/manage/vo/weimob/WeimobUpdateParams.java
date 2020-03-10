package com.mee.manage.vo.weimob;

import com.mee.manage.vo.SkuList;
import lombok.Data;

import java.util.List;

@Data
public class WeimobUpdateParams {

    Long goodsId;

    Integer operateType;

    Long storeId;

    List<SkuList> skuList;
}

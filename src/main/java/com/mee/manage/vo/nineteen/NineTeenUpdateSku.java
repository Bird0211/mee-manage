package com.mee.manage.vo.nineteen;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class NineTeenUpdateSku {
    Long skuId;

    BigDecimal skuPrice;

    BigDecimal firstLevel;

    BigDecimal secondLevel;

    BigDecimal thirdLevel;

    BigDecimal fourthLevel;

    BigDecimal fifthLevel;

    BigDecimal sixthLevel;

}
package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DiscountInfo {

    Integer deliveryUsedPoints;
    BigDecimal pointsDiscountDeliveryAmount;
    BigDecimal balanceDiscountDeliveryAmount;
    BigDecimal couponDiscountAmount;
    BigDecimal couponCodeDiscountAmount;
    BigDecimal promotionDiscountAmount;
    BigDecimal membershipDiscountAmount;
    Integer usedMemberPoints;
    BigDecimal memberPointsDiscountAmount;
    BigDecimal balanceDiscountAmount;
    BigDecimal merchantDiscountAmount;

}

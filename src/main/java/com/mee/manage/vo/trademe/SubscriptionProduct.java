package com.mee.manage.vo.trademe;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class SubscriptionProduct {
    
    Integer Id;

    BigDecimal Price;

    ProductPaymentInterval PaymentInterval;

    List<ProductBenefits> Benefits;
}
package com.mee.manage.vo.trademe;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class SoltItemPaymentDetails {
    
    Boolean IsPaymentPending;

    Integer PaymentType;

    BigDecimal PaymentAmount;

    BigDecimal PaymentMethodFee;

    Integer GstCollected;

    List<PaymentDetailRefundCollection> RefundCollection;

}
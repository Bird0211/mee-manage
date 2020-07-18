package com.mee.manage.vo.trademe;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentDetailRefundCollection {
    
    BigDecimal Amount;

    String Destination;

    String Date;

}
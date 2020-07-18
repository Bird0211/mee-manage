package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class ProductPaymentInterval {
    String Description;

    Integer IntervalType;

    Integer IntervalsPerPayment;
}
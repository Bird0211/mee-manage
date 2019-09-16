package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRate {

    String fromCurrency;

    String toCurrency;

    BigDecimal rate;
}

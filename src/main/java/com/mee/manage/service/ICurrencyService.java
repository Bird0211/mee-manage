package com.mee.manage.service;

import com.mee.manage.vo.ExchangeRate;

public interface ICurrencyService {

    ExchangeRate getExangeRage(String fromCurrency,String toCurrncy);

}

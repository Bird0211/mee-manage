package com.mee.manage.service;

import com.mee.manage.enums.WeimobDeliveryCompany;

/**
 * 快递鸟Service
 */
public interface IkdnService {

    WeimobDeliveryCompany identifyOrder(String order);


}

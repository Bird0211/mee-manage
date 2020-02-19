package com.mee.manage.service;


import com.mee.manage.enums.WeimobDeliveryCompany;

/**
 * 物流服务
 */
public interface IExpressService {


    WeimobDeliveryCompany getExpressComByCode(String code);

}

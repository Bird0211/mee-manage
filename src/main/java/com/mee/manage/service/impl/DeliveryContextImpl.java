package com.mee.manage.service.impl;

import java.util.Map;

import com.mee.manage.service.IDelivery;
import com.mee.manage.service.IDeliveryContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryContextImpl implements IDeliveryContext {

    @Autowired
    Map<String, IDelivery> deliveryMap;

    @Override
    public IDelivery getDeliveryService(String type) {
        return deliveryMap.get(type);
    }
    


}

package com.mee.manage.service;

import com.mee.manage.po.UggOrder;

public interface IDelivery {
    
    boolean postDelivery(UggOrder order, Integer bizId);

}

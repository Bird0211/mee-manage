package com.mee.manage.vo.delivery;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.mee.manage.enums.WeimobDeliveryCompany;
import com.mee.manage.po.UggOrder;
import com.mee.manage.service.IDelivery;
import com.mee.manage.service.IWeimobService;
import com.mee.manage.vo.DeliveryOrderVo;
import com.mee.manage.vo.DeliverySkuInfo;
import com.mee.manage.vo.weimob.WeimobDeliveryOrderResp;
import com.mee.manage.vo.weimob.WeimobOrderDetailVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("weimob")
public class Deliveryweimob implements IDelivery {

    private static final Logger logger = LoggerFactory.getLogger(IDelivery.class);


    @Autowired
    IWeimobService weimobService;

    @Override
    public boolean postDelivery(UggOrder order, Integer bizId) {
        if(order == null)
            return false;

        WeimobOrderDetailVo orderDetail = weimobService.getWeimobOrder(order.getExtId(), Long.parseLong(bizId.toString()));
        if(orderDetail == null) {
            logger.info("Weimob Order Detail is null ExtId = {} , BizId = {}", order.getExtId(), bizId);
            return false;
        }

        if((orderDetail.getOrderStatus() == 2 || orderDetail.getOrderStatus() == 3)) {
            return true;
        }
        String expressCode = WeimobDeliveryCompany.getExpCompany(order.getExpressName()) != null ? 
                                WeimobDeliveryCompany.getExpCompany(order.getExpressName()).getCode() : 
                                order.getExpressName();
        DeliveryOrderVo deleverOrder = new DeliveryOrderVo();
        deleverOrder.setAddress(order.getReceiveAddress());
        deleverOrder.setDeliveryId(order.getExpressId());
        deleverOrder.setExpressComCode(expressCode);
        deleverOrder.setId_num(null);
        deleverOrder.setName(order.getReceiveName());
        deleverOrder.setOrderId(order.getExtId());
        deleverOrder.setPhone(order.getReceivePhone());
        deleverOrder.setSplit(true);

        List<DeliverySkuInfo> skuInfos =  orderDetail.getItemList().stream().
                    filter(item -> item.getSkuCode().equals(order.getProductSku().toString())).
                    map(i -> {
            DeliverySkuInfo info = new DeliverySkuInfo();
            info.setItemId(i.getId());
            info.setSku(i.getSkuCode());
            info.setSkuId(i.getSkuId());
            info.setSkuNum(i.getSkuNum());
            return info;
        }).collect(Collectors.toList());

        deleverOrder.setSkuInfo(skuInfos);

        WeimobDeliveryOrderResp resp = weimobService.sendSingleOrder(deleverOrder, Long.parseLong(bizId.toString()));
        if (resp == null || resp.getCode() == null || resp.getData() == null || !resp.getData().getSuccess()) {
            return false;
        }
        return true;
    }
    
}

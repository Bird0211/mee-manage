package com.mee.manage.service;

import java.util.List;

import com.mee.manage.exception.MeeException;
import com.mee.manage.po.UggOrder;
import com.mee.manage.vo.ugg.OrderCountResult;
import com.mee.manage.vo.ugg.OrderListResult;
import com.mee.manage.vo.ugg.QueryOrder;
import com.mee.manage.vo.ugg.QueryOrderRsp;
import com.mee.manage.vo.ugg.QueryParams;
import com.mee.manage.vo.ugg.UggOrderData;
import com.mee.manage.vo.ugg.UggProductDetail;

public interface IUggService {
    
    String authToken(Long bizId, String username, String password) throws MeeException;

    String login(Long bizId, String username, String password) throws MeeException;

    String getToken(Long bizId) throws MeeException;

    String getLoginToken(Long bizId) throws MeeException;

    UggProductDetail getDetailBySKU(Long bizId, Long sku) throws MeeException;

    boolean saveUggOrder(UggOrder order, Long bizId) throws MeeException;

    OrderListResult getOrders(QueryParams params, Integer pageIndex, Integer pageSize) throws MeeException;

    List<OrderCountResult> getOrderCount(QueryParams params) throws MeeException;

    //创建批次ID
    String createBatchOrder(List<UggOrder> orders) throws MeeException;

    //订单已支付、订单代发
    boolean sendOrders(List<UggOrder> orders, Long bizId) throws MeeException;

    boolean sendOrder(UggOrder order, Long bizId) throws MeeException;

    //订单已发货
    void deliveryOrder(List<UggOrderData> orders) throws MeeException;

    QueryOrderRsp queryUggOrders(QueryOrder params, Long bizId) throws MeeException;

}

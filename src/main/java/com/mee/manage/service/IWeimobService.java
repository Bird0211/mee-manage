package com.mee.manage.service;

import com.mee.manage.exception.MeeException;
import com.mee.manage.vo.*;
import com.mee.manage.vo.weimob.WeimobDeliveryOrderResp;
import com.mee.manage.vo.weimob.WeimobDeliveryVo;
import com.mee.manage.vo.weimob.WeimobGroupVo;
import com.mee.manage.vo.weimob.WeimobOrderDataList;
import com.mee.manage.vo.weimob.WeimobOrderDetailVo;
import com.mee.manage.vo.weimob.WeimobOrderListRequest;
import com.mee.manage.vo.weimob.WeimobOrderListRespVo;
import com.mee.manage.vo.weimob.WeimobOrderListResponse;

import java.util.Date;
import java.util.List;

public interface IWeimobService {

    boolean addCode(String code,Long bizId);

    CheckTokenResult checkToken(Long bizId);

    String getToken(Long bizId);

    CheckTokenResult refreshToken(String refreshToken,Long bizId);

    boolean setToken(String token, Date expire,String refreshToken,Date expireRefreshToken,Long bizId);

    WeimobOrderListResponse getOrderList(WeimobOrderListRequest request,Long bizId);

    WeimobOrderListRespVo getList(WeimobOrderListRequest request,Long bizId);

    List<WeimobGroupVo> getClassifyInfo(Long bizId);

    WeimobOrderDetailVo getWeimobOrder(String orderId,Long bizId);

    List<GoodPageList> getGoodList(GoodListQueryParameter params,Long bizId);

    List<GoodInfoVo> getWeimobGoods(GoodListQueryParameter params,Long bizId);

    GoodDetailData getWeimobGoodDetail(Long goodId,Long bizId);

    List<PriceUpdateResult> updateWeimobPrice(List<GoodPriceDetail> goodsPrice,Long bizId);

    GoodInfoVo getWeimobGoodBySku(Long sku,Long bizId);

    boolean refreshWeimob(Long bizId);

    OrderDeliveryResult orderDelivery(List<DeliveryOrderVo> deleverOrders,Long bizId);

    boolean sendBathOrder(List<DeliveryOrderVo> deleverOrders,Long bizId);

    WeimobDeliveryOrderResp sendSingleOrder(DeliveryOrderVo deleverOrder, Long bizId);

    List<DeliveryOrderVo> sendSingleOrder(List<DeliveryOrderVo> deleverOrders,Long bizId);

    boolean flagOrders(Long bizId, Integer flagRank, List<String> orderIds);

    boolean flagLoadOrders(Long bizId, List<String> orderIds);

    List<WeimobOrderDataList> getDeliveryOrder(Long bizId, WeimobDeliveryVo request) throws MeeException;

}

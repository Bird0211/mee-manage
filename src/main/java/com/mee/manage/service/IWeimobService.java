package com.mee.manage.service;

import com.mee.manage.vo.*;

import java.util.Date;
import java.util.List;

public interface IWeimobService {

    boolean addCode(String code);

    CheckTokenResult checkToken();

    String getToken();

    CheckTokenResult refreshToken(String refreshToken);

    boolean setToken(String token, Date expire,String refreshToken,Date expireRefreshToken);

    MeeResult getOrderList(WeimobOrderListRequest request);

    List<WeimobGroupVo> getClassifyInfo();

    WeimobOrderDetailVo getWeimobOrder(String orderId);

    List<GoodPageList> getGoodList(GoodListQueryParameter params);

    List<GoodInfoVo> getWeimobGoods(GoodListQueryParameter params);

    GoodDetailData getWeimobGoodDetail(Long goodId);

    List<PriceUpdateResult> updateWeimobPrice(List<GoodPriceDetail> goodsPrice);

    GoodInfoVo getWeimobGoodBySku(Long sku);

    boolean refreshWeimob();

    OrderDeliveryResult orderDelivery(List<DeliveryOrderVo> deleverOrders);

    boolean sendBathOrder(List<DeliveryOrderVo> deleverOrders);

    List<DeliveryOrderVo> sendSigleOrder(List<DeliveryOrderVo> deleverOrders);

}

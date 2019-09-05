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

}

package com.mee.manage.service;

import java.util.List;

import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.trademe.SoltItemFilter;
import com.mee.manage.vo.trademe.TradeMeAccessToken;
import com.mee.manage.vo.trademe.TradeMeProfile;
import com.mee.manage.vo.trademe.TradeMeSoltOrder;
import com.mee.manage.vo.trademe.TradeMeTokenResult;

public interface ITradeMeService {

    MeeResult checkToken();

    TradeMeTokenResult requestToken(Long bizId);

    boolean accessToken(Long bizId, TradeMeAccessToken accessToken);
    
    TradeMeProfile getProfile(Long bizId, Integer platFormId);

    List<TradeMeSoltOrder> getSoltItem(Integer platFormId, SoltItemFilter filter);

}

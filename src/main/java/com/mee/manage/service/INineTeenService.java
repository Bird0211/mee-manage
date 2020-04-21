package com.mee.manage.service;

import com.mee.manage.vo.OrderListResponse;
import com.mee.manage.vo.nineteen.SearchVo;

/**
 * INineTeenService
 */
public interface INineTeenService {

    OrderListResponse queryOrderList(SearchVo searchVo, Integer platformId);
}
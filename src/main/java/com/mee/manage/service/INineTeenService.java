package com.mee.manage.service;

import java.util.List;

import com.mee.manage.vo.OrderListResponse;
import com.mee.manage.vo.nineteen.NineTeenProductGroupVo;
import com.mee.manage.vo.nineteen.NineTeenProductParam;
import com.mee.manage.vo.nineteen.NineTeenProductResponse;
import com.mee.manage.vo.nineteen.NineTeenProductTypeVo;
import com.mee.manage.vo.nineteen.NineTeenUpdatePrice;
import com.mee.manage.vo.nineteen.SearchVo;

/**
 * INineTeenService
 */
public interface INineTeenService {

    OrderListResponse queryOrderList(SearchVo searchVo, Integer platformId);

    List<NineTeenProductTypeVo> getProductType(Integer platformId, Integer typeId);

    List<NineTeenProductGroupVo> getProductGroup(Integer platformId);

    NineTeenProductResponse getProduct(Integer platformId, NineTeenProductParam param);

    boolean updatePrice(Integer platformId, List<NineTeenUpdatePrice> updatePrice);
}
package com.mee.manage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.mee.manage.config.NineTeenConfig;
import com.mee.manage.po.PlatformConfig;
import com.mee.manage.service.INineTeenService;
import com.mee.manage.service.IPlatformConfigService;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.vo.OrderItem;
import com.mee.manage.vo.OrderListResponse;
import com.mee.manage.vo.ProductVo;
import com.mee.manage.vo.nineteen.NineTeenData;
import com.mee.manage.vo.nineteen.NineTeenOrder;
import com.mee.manage.vo.nineteen.NineTeenOrderDetail;
import com.mee.manage.vo.nineteen.NineTeenResponse;
import com.mee.manage.vo.nineteen.SearchVo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * NineTeenServiceImpl
 */
@Service
public class NineTeenServiceImpl implements INineTeenService {

    protected static final Logger logger = LoggerFactory.getLogger(INineTeenService.class);


    @Autowired
    IPlatformConfigService platformService;

    @Autowired
    NineTeenConfig config;

    @Override
    public OrderListResponse queryOrderList(SearchVo searchVo, Integer platformId) {
        if(platformId == null || platformId <= 0) {
            return null;
        }

        PlatformConfig platformConfig = platformService.getPlatFormById(platformId);
        if (platformConfig == null) {
            return null;
        }

        Map<String,Object> params = new HashMap<>();
        params.put("account_id", platformConfig.getClientId());
        params.put("account_token", platformConfig.getToken());
        params.put("page_size", 100);
        params.put("page", 1);

        if(StringUtils.isNotBlank(searchVo.getCreateStartTime()))
            params.put("create_start_time",searchVo.getCreateStartTime());

        if(StringUtils.isNotBlank(searchVo.getCreateEndTime()))
            params.put("create_end_time", searchVo.getCreateEndTime());

        if(StringUtils.isNotBlank(searchVo.getPayStartTime())) 
            params.put("pay_start_time", searchVo.getPayStartTime());
        
        if(StringUtils.isNotBlank(searchVo.getPayEndTime()))
            params.put("pay_end_time", searchVo.getPayEndTime());
        
        if(searchVo.getState() != null)
            params.put("state", searchVo.getState().toString());

        logger.info("params = {}" , params);
        List<NineTeenResponse> responses = new ArrayList<>();
        postData(params,responses);

        if(responses.isEmpty()) {
            return null;
        }

        int totalCount = responses.get(0).getData().getTotal();
        List<OrderItem> items = new ArrayList<>();
        for (NineTeenResponse nineTeenResponse : responses) {
            if(nineTeenResponse.getCode() != 200) {
                continue;
            }

            NineTeenData data = nineTeenResponse.getData();
            List<NineTeenOrder> orders = data.getData();
            
            for(NineTeenOrder order : orders) {
                OrderItem item = new OrderItem();
                item.setAddress(order.getCollection_address());
                item.setIdCardNo(order.getIdentity_number());
                item.setName(StringUtils.isEmpty(order.getIdentity_name()) ? order.getCollection_name() : order.getIdentity_name());
                int total = 0;
                item.setOrderNo(order.getTrade_no());
                item.setPhone(order.getCollection_phone());
                item.setRemark(order.getRemarks());
                item.setSender(order.getSender_name());
                item.setSenderPhone(order.getPhone());

                List<ProductVo> products = new ArrayList<>();
                for(NineTeenOrderDetail orderDetail : order.getOrder_detail()) {
                    total += orderDetail.getNum();

                    ProductVo productVo = new ProductVo();
                    productVo.setNum(orderDetail.getNum());
                    productVo.setContent(orderDetail.getName());
                    productVo.setSku(orderDetail.getCode());
                    products.add(productVo);
                }
                item.setProducts(products);
                item.setNum(total);
                items.add(item);
            }


        }

        OrderListResponse  orderList = new OrderListResponse();
        orderList.setPageNum(1);
        orderList.setPageSize(100);
        orderList.setTotalCount(totalCount);
        orderList.setItems(items);
        return orderList;
    }

    private void postData (Map<String,Object> params,List<NineTeenResponse> responses) {
        if(params == null)
            return ;

        String url = config.getOrderListUrl();

        String result = JoddHttpUtils.sendPost(url, params);
        logger.info("result = {}",result);

        if(StringUtils.isEmpty(result)) {
            return ;
        }

        NineTeenResponse response = null;
        try {
            response = JSON.parseObject(result, NineTeenResponse.class);
        } catch (Exception ex){
            logger.error("nineteen to json errro {}", result, ex);
        }

        if(response == null || response.getCode() == null || response.getCode() != 200)
            return ;
        
        responses.add(response);
        NineTeenData nineteenData = response.getData();
        if(nineteenData == null || nineteenData.getCurrent_page() == nineteenData.getLast_page())
            return;
        
        
        if(nineteenData.getData() == null || nineteenData.getData().isEmpty()) {
            return;
        }

        params.put("page", nineteenData.getCurrent_page()+1);
        postData(params,responses);
    }
    

    
}
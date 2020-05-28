package com.mee.manage.service;

import java.util.List;

import com.mee.manage.po.PlatformConfig;
import com.mee.manage.vo.FlywayOrderResponse;
import com.mee.manage.vo.FlywayorderDetail;

public interface IFlywayService {

    boolean authToken(Long bizId, String username, String password);

    boolean checkAuth(Long bizId);

    PlatformConfig getPlatformConfig(Long bizId);
    
    List<FlywayOrderResponse> addOrders(Long bizId, List<FlywayorderDetail> orderDetailList);

    boolean setNamePwd(Long bizId, String username,String password);

    String[] getNamePwd(Long bizId);
}
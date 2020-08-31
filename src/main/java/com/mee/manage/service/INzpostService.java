package com.mee.manage.service;

import java.util.List;

import com.mee.manage.exception.MeeException;
import com.mee.manage.vo.nzpost.LabelStatusResult;
import com.mee.manage.vo.nzpost.NzPostLabelResult;
import com.mee.manage.vo.nzpost.NzPostLabelStatus;
import com.mee.manage.vo.nzpost.ShippedOptionReq;
import com.mee.manage.vo.nzpost.ShippedOptionService;
import com.mee.manage.vo.trademe.ShippedItem;

public interface INzpostService {

    String getToken(Long bizId);

    List<NzPostLabelResult> shippedItem(Long nzPostId , ShippedItem items) throws MeeException;

    List<ShippedOptionService> shippedItem(ShippedOptionReq entity) throws MeeException;

    NzPostLabelStatus labelStatus(String consignmentId) throws MeeException;

    List<LabelStatusResult> levelStatus(String[] consignmentId) throws MeeException;
    
}
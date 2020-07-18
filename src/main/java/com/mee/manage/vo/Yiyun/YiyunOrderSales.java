package com.mee.manage.vo.Yiyun;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class YiyunOrderSales {
    
    Long id;

    String externalId;

    String customer;

    String total;

    String logisticInfo;

    String date;

    String fullTotal;

    String comment;

    String creator;

    String status;

    String[] details;

    List<YiyunOrderDetail> orderDetail;

    YiyunlogisticInfo logistic;

    public List<YiyunOrderDetail> getorderDetail() {
        List<YiyunOrderDetail> orderDetails = null;
        if(this.details != null && this.details.length > 0) {
            for(String detail : this.details) {
                if(StringUtils.isEmpty(detail)) {
                    continue;
                }
                YiyunOrderDetail oDetail = new YiyunOrderDetail(detail);
                if(orderDetails == null) {
                    orderDetails = Lists.newArrayList();
                }

                orderDetails.add(oDetail);
            }
        }
        this.orderDetail = orderDetails;
        return this.orderDetail;
    }

    public YiyunlogisticInfo getLogistic() {
        this.logistic = new YiyunlogisticInfo(this.logisticInfo);
        return this.logistic;
    }

}
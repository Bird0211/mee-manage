package com.mee.manage.vo.weimob;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WeimobOrderDataList {
    Long orderNo;
    Long pid;
    Long wid;
    String userNickname;
    Integer orderStatus;
    String orderStatusName;
    Integer deliveryType;
    Integer bizType;
    Integer subBizType;
    String bizOrderId;
    Long confirmReceivedTime;
    Long deliveryTime;
    Integer enableDelivery;
    String deliveryTypeName;
    BigDecimal paymentAmount;
    BigDecimal deliveryAmount;
    Integer channelType;
    String channelTypeName;
    Integer paymentType;
    String paymentTypeName;
    Integer paymentStatus;
    String paymentMethodName;
    Long createTime;
    Long updateTime;
    Long paymentTime;
    Long totalPoint;
    Integer transferType;
    Integer transferStatus;
    String transferFailReason;
    String selfPickupSiteName;
    String processStoreTitle;
    Long processStoreId;
    Long storeId;
    String storeTitle;
    Integer flagRank;
    String flagContent;
    List<WeimobItem> itemList;
    String buyerRemark;
    String receiverName;
    String receiverMobile;
    String receiverAddress;
    String expectDeliveryTime;
    Long deliveryOrderId;

}

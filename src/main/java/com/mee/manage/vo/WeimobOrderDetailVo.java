package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class WeimobOrderDetailVo {

    Date autoCancelTime;

    Date autoConfirmReceivedTime;

    WeimobBizInfoVo bizInfo;

    WeimibBuyerInfoVo buyerInfo;

    String buyerRemark;

    Date cancelTime;

    Integer cancelType;

    String channelTypeName;

    String commentInfo;

    Date confirmReceivedTime;

    Date createTime;

    List<WeimobCustomFieldListVo> customFieldList;

    BigDecimal deliveryAmount;

    WeimobDeliveryDetailVo deliveryDetail;

    BigDecimal deliveryDiscountAmount;

    BigDecimal deliveryPaymentAmount;

    Date deliveryTime;

    DiscountInfo discountInfo;

    Integer enableDelivery;

    String flagContent;

    Integer flagRank;

    BigDecimal goodsAmount;

    Long orderNo;

    Integer orderStatus;

    String orderStatusName;

    BigDecimal paymentAmount;

    BigDecimal shouldPaymentAmount;

    BigDecimal totalAmount;

    Long totalPoint;

    Integer transferStatus;

    Integer transferType;

    Integer transferTypeReality;

    List<OrderItemFullInfoVo> itemList;
}

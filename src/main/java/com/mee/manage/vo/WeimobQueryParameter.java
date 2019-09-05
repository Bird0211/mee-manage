package com.mee.manage.vo;

import lombok.Data;

@Data
public class WeimobQueryParameter {

    Long createStartTime;

    Long createEndTime;

    Long updateStartTime;

    Long updateEndTime;

    String keyword;

    Integer searchType;

    Integer[] channelTypes; //渠道类型 (空为不限,0-公众号,1-小程序,2-H5,3-QQ,4-微博,5-头条,6-支付宝,7-线下)

    Integer[] orderTypes; //订单类型： 1、B2C订单 99、充值订单 97、直接消费订单 不传默认为 1

    Integer[] orderStatuses; //订单状态 (空为不限,0-待支付,1-待发货,2-已发货,3-已完成,4-已取消)

    Integer[] paymentMethods;//支付方式 (空为不限,1-支付宝,2-微信,3-银行卡,4-现金,5-货到付款,6-无需支付)

    Integer[] paymentTypes;//支付类型 (空为不限,1-线上支付（包括微信、支付宝、银行卡、购物卡等）, 2-线下支付（包括现金、欠条等，默认是现金）, 3-混合支付（网上支付和线下支付混合）)

    Integer[] flagRanks;//订单标记 (空为不限,等级1到5)

    Integer[] bizTypes;//业务类型 (空为不限,0-普通,3-限时折扣,4-砍价, 5-拼团)

    Integer[] deliveryStatuses; //发货状态 (空为不限,0未发货，1为部分发货，2为已发货)


}

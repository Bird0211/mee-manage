package com.mee.manage.vo.trademe;

import java.util.List;

import lombok.Data;

@Data
public class TradeMeSoldOrderResp {
    
    List<TradeMeSoltOrder> emailSent;
    List<TradeMeSoltOrder> paymentReceived;
    List<TradeMeSoltOrder> goodsShipped;
    List<TradeMeSoltOrder> saleCompleted;

}
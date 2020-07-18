package com.mee.manage.vo;

import lombok.Data;

@Data
public class OrderParamVo {
    
    String biz_id;
    String token;
    String time;
    String from;
    String to;
    String nonce;
    String cont;
    String sign;

}
package com.mee.manage.vo.weimob;

import lombok.Data;

@Data
public class WeimobItemsResponse {
    String content;
    int num;
    String address;
    String phone;
    String name;
    Long orderNo;
    String idCardNo;    //身份证号
}

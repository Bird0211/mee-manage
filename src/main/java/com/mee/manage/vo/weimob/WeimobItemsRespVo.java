package com.mee.manage.vo.weimob;

import java.util.List;

import lombok.Data;

@Data
public class WeimobItemsRespVo {
    List<WeimobItemVo> items;
    int num;
    String address;
    String phone;
    String name;
    Long orderNo;
    String idCardNo;    //身份证号
}

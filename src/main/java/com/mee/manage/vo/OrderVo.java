package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderVo {

    private String orderId;
    private String name;
    private String id_num;
    private String phone;
    private String address;
    private String sender;
    private List<ProductVo> product;
    private Long expId;
    private Integer num;
}

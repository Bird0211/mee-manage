package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class SettleVo {

    private Long userId;

    private String userName;

    private List<OrderVo> order;

}

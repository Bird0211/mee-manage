package com.mee.manage.vo;

import java.util.List;

import lombok.Data;

@Data
public class FlywayorderVo {

    Integer clientID;

    List<FlywayorderDetail> orderDetailList;
    
}
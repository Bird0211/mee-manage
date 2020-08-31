package com.mee.manage.vo.weimob;

import java.util.List;

import lombok.Data;

@Data
public class WeimobFlagOrderParam {
    
    Integer flagRank;

    String flagContent;

    List<Long> orderNoList;

}
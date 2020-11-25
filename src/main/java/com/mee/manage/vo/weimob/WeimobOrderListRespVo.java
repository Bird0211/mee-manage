package com.mee.manage.vo.weimob;

import java.util.List;

import lombok.Data;

@Data
public class WeimobOrderListRespVo {
    
    int pageNum;
    int pageSize;
    int totalCount;
    List<WeimobItemsRespVo> items;

}

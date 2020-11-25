package com.mee.manage.vo.ugg;

import java.util.List;

import com.mee.manage.po.UggOrder;

import lombok.Data;

@Data
public class OrderListResult {
    
    Long total;

    Long pageIndex;

    Long pageSize;

    List<UggOrder> orders;

}

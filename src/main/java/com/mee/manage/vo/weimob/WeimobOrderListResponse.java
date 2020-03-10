package com.mee.manage.vo.weimob;

import lombok.Data;

import java.util.List;

@Data
public class WeimobOrderListResponse {

    int pageNum;
    int pageSize;
    int totalCount;
    List<WeimobItemsResponse> items;


}

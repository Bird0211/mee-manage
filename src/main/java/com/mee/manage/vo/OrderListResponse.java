package com.mee.manage.vo;

import java.util.List;

import lombok.Data;

/**
 * OrderListResponse
 */

 @Data
public class OrderListResponse {

    int pageNum;
    int pageSize;
    int totalCount;
    List<OrderItem> items;
}
package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderDeliveryResult {
    boolean success;

    List<String> errorOrderIds;
}

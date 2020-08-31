package com.mee.manage.vo.nzpost;

import java.util.List;

import lombok.Data;

@Data
public class ShippedOptionResp {
    Boolean success;

    Double rated_weight;

    String message_id;

    List<ShippedOptionService> services;

    List<ShippedError> errors;
}
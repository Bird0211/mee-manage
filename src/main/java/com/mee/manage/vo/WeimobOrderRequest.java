package com.mee.manage.vo;

import lombok.Data;

@Data
public class WeimobOrderRequest {

    int pageNum;
    int pageSize;
    WeimobQueryParameter queryParameter;

}

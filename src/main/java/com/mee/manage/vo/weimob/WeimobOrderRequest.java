package com.mee.manage.vo.weimob;

import lombok.Data;

@Data
public class WeimobOrderRequest {

    int pageNum;
    int pageSize;
    WeimobQueryParameter queryParameter;

}

package com.mee.manage.vo;

import lombok.Data;

@Data
public class GoodListRequest {
    Integer pageNum;

    Integer pageSize;

    GoodListQueryParameter queryParameter;

}

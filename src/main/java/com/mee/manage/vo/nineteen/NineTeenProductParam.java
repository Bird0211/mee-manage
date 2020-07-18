package com.mee.manage.vo.nineteen;

import lombok.Data;

@Data
public class NineTeenProductParam {
    int pageSize;
    int page;
    Integer typeId;
    String goodName;
    String groupId;
    String skuCode;
}
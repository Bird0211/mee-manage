package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class WeimobStoreData {
    Integer totalCount;

    Integer pageSize;

    Integer pageNum;

    List<StoreDetail> pageList;

    HeadStoreInfo headStoreInfo;


}

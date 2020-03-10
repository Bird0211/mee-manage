package com.mee.manage.vo.weimob;

import com.mee.manage.vo.HeadStoreInfo;
import com.mee.manage.vo.StoreDetail;
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

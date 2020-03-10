package com.mee.manage.vo.weimob;

import lombok.Data;

import java.util.List;

@Data
public class WeimobOrderData {

    Integer pageNum;

    Integer pageSize;

    Integer totalCount;

    List<WeimobOrderDataList> pageList;

}

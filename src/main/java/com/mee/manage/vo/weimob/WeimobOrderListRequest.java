package com.mee.manage.vo.weimob;

import lombok.Data;

import java.util.Date;

@Data
public class WeimobOrderListRequest {
    Integer pageNum;
    Integer pageSize;
    Date createStartTime;
    Date createEndTime;

    Integer orderStatuses;
    Integer orderType;  //0:奶粉;1:其他
    Integer sendarea;
    Integer flagRanks;

}

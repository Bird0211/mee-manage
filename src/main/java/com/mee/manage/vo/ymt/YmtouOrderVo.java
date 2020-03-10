package com.mee.manage.vo.ymt;

import lombok.Data;

import java.util.List;

@Data
public class YmtouOrderVo {

    Integer total;

    boolean needPrintLog;

    List<YmtouOrderInfo> orders_info;


}

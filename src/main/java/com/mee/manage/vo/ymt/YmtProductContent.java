package com.mee.manage.vo.ymt;

import lombok.Data;

import java.util.List;

@Data
public class YmtProductContent {

    boolean needPrintLog;

    Integer total_count;

    List<YmtouProduct> product_infos;
}

package com.mee.manage.vo.trademe;

import lombok.Data;
import java.util.List;


@Data
public class SoltItemResponse {
    Integer TotalCount;

    Integer Page;

    Integer PageSize;

    List<SoltItemList> List;
}
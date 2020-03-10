package com.mee.manage.vo;

import com.mee.manage.vo.weimob.WeimobOrderCode;
import lombok.Data;

@Data
public class GoodDetailResponse {

    WeimobOrderCode code;

    GoodDetailData data;

}

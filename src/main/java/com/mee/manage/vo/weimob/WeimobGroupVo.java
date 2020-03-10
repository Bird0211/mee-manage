package com.mee.manage.vo.weimob;

import lombok.Data;

import java.util.List;

@Data
public class WeimobGroupVo {

    String title;

    Long classifyId;

    int level;

    List<WeimobGroupVo> childrenGroup;

}

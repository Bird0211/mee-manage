package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class GoodsClassify {

    String title;
    int level;
    Long classifyId;
    String imageUrl;
    List<GoodsClassify> childrenClassify;

}

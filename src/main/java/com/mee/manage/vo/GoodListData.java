package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class GoodListData {


    Integer pageNum;

    Integer totalCount;

    List<GoodPageList> pageList;

}

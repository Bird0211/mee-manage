package com.mee.manage.vo;

import java.util.List;

import com.mee.manage.po.News;

import lombok.Data;

@Data
public class NewsPageResult {
    Long total;

    Long pageIndex;

    Long pageSize;

    List<News> news;
}
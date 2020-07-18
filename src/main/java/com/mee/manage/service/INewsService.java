package com.mee.manage.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.News;
import com.mee.manage.vo.NewsIOInfo;
import com.mee.manage.vo.NewsPageResult;
import com.mee.manage.vo.NewsParam;

public interface INewsService extends IService<News> {
    
    List<NewsIOInfo> getNews(Long bizId);

    boolean addNews(News news);

    boolean updateNews(News news);

    NewsPageResult getNews(NewsParam param);
    
}
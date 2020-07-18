package com.mee.manage.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.config.Config;
import com.mee.manage.mapper.INewsMapper;
import com.mee.manage.po.News;
import com.mee.manage.service.INewsService;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.vo.NewsIO;
import com.mee.manage.vo.NewsIOInfo;
import com.mee.manage.vo.NewsPageResult;
import com.mee.manage.vo.NewsParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl extends ServiceImpl<INewsMapper, News> implements INewsService {

    @Autowired
    Config config;

    private static String APK_KEY = "76f7cedfd67d4e05b0d21c7aebbff71f";

    @Override
    public List<NewsIOInfo> getNews(Long bizId) {
        String url = config.getNewsUrl();
        Map<String, String> params = new HashMap<>();
        params.put("apiKey", APK_KEY);
        params.put("country", "nz");
        params.put("pageSize", "5");
        params.put("page", "1");

        String result = JoddHttpUtils.getData(url, params);
        NewsIO nIo = null;
        if (result != null) {
            nIo = JSON.parseObject(result, NewsIO.class);
        }

        if (nIo != null && nIo.getStatus().equals("ok")) {
            return nIo.getArticles();
        }
        return null;
    }

    @Override
    public boolean addNews(News news) {
        news.setUpdateDate(new Date());
        return save(news);
    }

    @Override
    public boolean updateNews(News news) {
        news.setUpdateDate(new Date());
        return updateById(news);
    }

    @Override
    public NewsPageResult getNews(NewsParam param) {
        NewsPageResult result = new NewsPageResult();
        Page<News> ipage = new Page<>(param.getPageIndex(), param.getPageSize());
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("update_date");

        IPage<News> pageResult = page(ipage, queryWrapper);
        if (pageResult != null && pageResult.getRecords() != null) {
            result.setNews(pageResult.getRecords());
            result.setTotal(pageResult.getTotal());
            result.setPageIndex(pageResult.getCurrent());
            result.setPageSize(pageResult.getPages());
        } else {
            result.setNews(null);
            result.setTotal(0L);
            result.setPageIndex(param.getPageIndex());
            result.setPageSize(param.getPageSize());
        }
        return result;
    }

    
    
}
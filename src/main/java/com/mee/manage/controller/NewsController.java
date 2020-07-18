package com.mee.manage.controller;

import com.mee.manage.po.News;
import com.mee.manage.service.INewsService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.NewsPageResult;
import com.mee.manage.vo.NewsParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController extends BaseController {

    @Autowired
    INewsService newsService;
    
    @RequestMapping(value = "/list/{bizId}", method = RequestMethod.GET)
    public MeeResult getNews(@PathVariable("bizId") Long bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            meeResult.setData(newsService.getNews(bizId));
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        } catch (Exception ex) {
            logger.error("getNews Error bizId = {}", bizId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    };

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public MeeResult addNews(@RequestBody News news) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = newsService.addNews(news);
            if(flag)
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } catch (Exception ex) {
            logger.error("addNews Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public MeeResult updateNews(@RequestBody News news) {
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = newsService.updateNews(news);
            if(flag)
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } catch (Exception ex) {
            logger.error("addNews Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }

    @RequestMapping(value = "/all", method = RequestMethod.POST)
    public MeeResult getNews(@RequestBody NewsParam param) {
        MeeResult meeResult = new MeeResult();
        try {
            NewsPageResult result = newsService.getNews(param);
            if(result != null) {
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
                meeResult.setData(result);
            }
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } catch (Exception ex) {
            logger.error("addNews Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }
    
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public MeeResult newsDetail(@PathVariable("id") Long id) {
        MeeResult meeResult = new MeeResult();
        try {
            News result = newsService.getById(id);
            if(result != null) {
                meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
                meeResult.setData(result);
            }
            else
                meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } catch (Exception ex) {
            logger.error("newsDetail {id} Error",id, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }

        return meeResult;
    }
}
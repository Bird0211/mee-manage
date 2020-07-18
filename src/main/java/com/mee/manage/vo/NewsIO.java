package com.mee.manage.vo;

import java.util.List;

import lombok.Data;

@Data
public class NewsIO {
    
    String status;
    Integer totalResults;
    List<NewsIOInfo> articles;
}
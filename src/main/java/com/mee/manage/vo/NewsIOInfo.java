package com.mee.manage.vo;

import lombok.Data;

@Data
public class NewsIOInfo {
    
    String author;
    String title;
    String description;
    String url;
    String urlToImage;
    String publishedAt;
    String content;
    NewsSource source;
}
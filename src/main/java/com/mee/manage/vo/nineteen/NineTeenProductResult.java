package com.mee.manage.vo.nineteen;

import java.util.List;

import lombok.Data;

@Data
public class NineTeenProductResult {
    
    int current_page;
    String first_page_url;
    int from;
    int last_page;
    String last_page_url;
    String next_page_url;
    String path;
    int per_page;
    String prev_page_url;
    int to;
    int total;

    List<ProductData> data;



}
package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class ResultVo {

    private int page;

    private int per_page;

    private int total;

    private int total_pages;

    private List<Object> data;

}

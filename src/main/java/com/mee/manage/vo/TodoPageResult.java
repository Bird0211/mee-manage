package com.mee.manage.vo;

import java.util.List;

import com.mee.manage.po.Todo;

import lombok.Data;

/**
 * TodoPageResult
 */
@Data
public class TodoPageResult {

    Long total;

    Long pageIndex;

    Long pageSize;

    List<Todo> todo;
    
}
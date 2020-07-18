package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.Todo;
import com.mee.manage.vo.PageVo;
import com.mee.manage.vo.TodoPageResult;

public interface ITodoService extends IService<Todo> {
    
    boolean addTodo(Todo todo);

    boolean editTodo(Long todoId, String title, Long uid);

    boolean removeTodo(Long todoId);

    boolean finishiTodo(Long todoId);

    boolean unSetTodo(Long todoId);

    TodoPageResult getMyTodo(Long bizId, Long userId, PageVo page);

    TodoPageResult getMyAllTodo(Long bizId, Long userId, PageVo page);

    TodoPageResult getCreatedTodo(Long bizId, Long userId, PageVo page);

    Integer getTodoNumber(Long bizId, Long userId);

    Integer getCreatedNumber(Long bizId, Long userId);

}
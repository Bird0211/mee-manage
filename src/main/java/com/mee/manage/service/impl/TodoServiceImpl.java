package com.mee.manage.service.impl;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.enums.TodoStatusEnm;
import com.mee.manage.mapper.ITodoMapper;
import com.mee.manage.po.Todo;
import com.mee.manage.service.ITodoService;
import com.mee.manage.vo.PageVo;
import com.mee.manage.vo.TodoPageResult;

import org.springframework.stereotype.Service;

@Service
public class TodoServiceImpl extends ServiceImpl<ITodoMapper, Todo> implements ITodoService{

	@Override
	public boolean addTodo(Todo todo) {
        todo.setCreateDate(new Date());
		return save(todo);
	}

	@Override
	public boolean editTodo(Long todoId, String title, Long uid) {
        Todo todo = new Todo();
        todo.setId(todoId);
        todo.setTitle(title);
        todo.setUid(uid);
		return updateById(todo);
	}

	@Override
	public boolean removeTodo(Long todoId) {
		return removeById(todoId);
	}

	@Override
	public boolean finishiTodo(Long todoId) {
		Todo todo = new Todo();
        todo.setId(todoId);
        todo.setStatus(TodoStatusEnm.DONE.getCode());
		return updateById(todo);
	}

	@Override
	public boolean unSetTodo(Long todoId) {
		Todo todo = new Todo();
        todo.setId(todoId);
        todo.setStatus(TodoStatusEnm.UNDO.getCode());
		return updateById(todo);
	}

	@Override
	public TodoPageResult getMyTodo(Long bizId, Long userId, PageVo page) {
        TodoPageResult result = new TodoPageResult();
        Page<Todo> ipage = new Page<>(page.getPageNo(), page.getPageRows());
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.eq("uid", userId);
        queryWrapper.eq("status", TodoStatusEnm.UNDO.getCode());

        queryWrapper.orderByDesc("create_date");

        IPage<Todo> pageResult = page(ipage, queryWrapper);
        if (pageResult != null && pageResult.getRecords() != null) {
            result.setTodo(pageResult.getRecords());
            result.setTotal(pageResult.getTotal());
            result.setPageIndex(pageResult.getCurrent());
            result.setPageSize(pageResult.getPages());
        } else {
            result.setTodo(null);
            result.setTotal(0L);
            result.setPageIndex(Long.valueOf(page.getPageNo()));
            result.setPageSize(Long.valueOf(page.getPageRows()));
        }
        
		return result;
	}

	@Override
	public TodoPageResult getCreatedTodo(Long bizId, Long userId, PageVo page) {
        TodoPageResult result = new TodoPageResult();
        Page<Todo> ipage = new Page<>(page.getPageNo(), page.getPageRows());
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.eq("create_uid", userId);
        queryWrapper.ne("uid", userId);

        queryWrapper.orderByDesc("create_date");

        IPage<Todo> pageResult = page(ipage, queryWrapper);
        if (pageResult != null && pageResult.getRecords() != null) {
            result.setTodo(pageResult.getRecords());
            result.setTotal(pageResult.getTotal());
            result.setPageIndex(pageResult.getCurrent());
            result.setPageSize(pageResult.getPages());
        } else {
            result.setTodo(null);
            result.setTotal(0L);
            result.setPageIndex(Long.valueOf(page.getPageNo()));
            result.setPageSize(Long.valueOf(page.getPageRows()));
        }
        
		return result;
	}

	@Override
	public Integer getTodoNumber(Long bizId, Long userId) {
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.eq("uid", userId);
        queryWrapper.eq("status", TodoStatusEnm.UNDO.getCode());

		return count(queryWrapper);
	}

	@Override
	public TodoPageResult getMyAllTodo(Long bizId, Long userId, PageVo page) {
		TodoPageResult result = new TodoPageResult();
        Page<Todo> ipage = new Page<>(page.getPageNo(), page.getPageRows());
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.eq("uid", userId);

        queryWrapper.orderByDesc("create_date");

        IPage<Todo> pageResult = page(ipage, queryWrapper);
        if (pageResult != null && pageResult.getRecords() != null) {
            result.setTodo(pageResult.getRecords());
            result.setTotal(pageResult.getTotal());
            result.setPageIndex(pageResult.getCurrent());
            result.setPageSize(pageResult.getPages());
        } else {
            result.setTodo(null);
            result.setTotal(0L);
            result.setPageIndex(Long.valueOf(page.getPageNo()));
            result.setPageSize(Long.valueOf(page.getPageRows()));
        }
        
		return result;
	}

	@Override
	public Integer getCreatedNumber(Long bizId, Long userId) {
		QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.eq("create_uid", userId);
        queryWrapper.ne("uid", userId);
        queryWrapper.eq("status", TodoStatusEnm.UNDO.getCode());

		return count(queryWrapper);
	}
    
}
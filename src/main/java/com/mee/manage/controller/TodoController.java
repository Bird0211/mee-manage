package com.mee.manage.controller;

import com.mee.manage.exception.MeeException;
import com.mee.manage.po.Todo;
import com.mee.manage.service.ITodoService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import com.mee.manage.vo.PageVo;
import com.mee.manage.vo.TodoPageResult;

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
@RequestMapping("/api/todo")
@CrossOrigin
public class TodoController extends BaseController {

    @Autowired
    ITodoService todoService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public MeeResult addTodo(@RequestBody Todo todo){
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = todoService.addTodo(todo);
            if(flag)
                meeResult.setStatusCodeDes(StatusCode.SUCCESS);
            else {
                meeResult.setStatusCodeDes(StatusCode.FAIL);
            }

        } catch (MeeException meeEx) {
            logger.error("addTodo Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("addTodo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    };

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public MeeResult saveTodo(@RequestBody Todo todo){
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = todoService.editTodo(todo.getId(), todo.getTitle(), todo.getUid());
            if(flag)
                meeResult.setStatusCodeDes(StatusCode.SUCCESS);
            else {
                meeResult.setStatusCodeDes(StatusCode.FAIL);
            }

        } catch (MeeException meeEx) {
            logger.error("addTodo Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("addTodo Error", ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    };

    @RequestMapping(value = "/remove/{todoId}", method = RequestMethod.DELETE)
    public MeeResult delTodo(@PathVariable("todoId") Long todoId){
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = todoService.removeTodo(todoId);
            if(flag)
                meeResult.setStatusCodeDes(StatusCode.SUCCESS);
            else 
                meeResult.setStatusCodeDes(StatusCode.FAIL);
            

        } catch (MeeException meeEx) {
            logger.error("remove Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("remove Error bizId = {}", todoId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    };

    @RequestMapping(value = "/mytodo/{bizId}/{uid}/{pageNo}/{pageSize}", method = RequestMethod.GET)
    public MeeResult getTodo(@PathVariable("bizId") Long bizId, 
                             @PathVariable("uid") Long uid,
                             @PathVariable("pageNo") Integer pageNo,
                             @PathVariable("pageSize") Integer pageSize
                             ){
        MeeResult meeResult = new MeeResult();
        try {
            PageVo pageVo = new PageVo();
            pageVo.setPageNo(pageNo);
            pageVo.setPageRows(pageSize);
            TodoPageResult result = todoService.getMyTodo(bizId , uid, pageVo);
            meeResult.setData(result);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);

        } catch (MeeException meeEx) {
            logger.error("getTodo Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("getTodo Error bizId = {}", bizId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    };

    @RequestMapping(value = "/myalltodo/{bizId}/{uid}/{pageNo}/{pageSize}", method = RequestMethod.GET)
    public MeeResult getAllTodo(@PathVariable("bizId") Long bizId, 
                             @PathVariable("uid") Long uid,
                             @PathVariable("pageNo") Integer pageNo,
                             @PathVariable("pageSize") Integer pageSize
                             ){
        MeeResult meeResult = new MeeResult();
        try {
            PageVo pageVo = new PageVo();
            pageVo.setPageNo(pageNo);
            pageVo.setPageRows(pageSize);
            TodoPageResult result = todoService.getMyAllTodo(bizId , uid, pageVo);
            meeResult.setData(result);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);

        } catch (MeeException meeEx) {
            logger.error("getTodo Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("getTodo Error bizId = {}", bizId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    };

    @RequestMapping(value = "/mycreated/{bizId}/{uid}/{pageNo}/{pageSize}", method = RequestMethod.GET)
    public MeeResult getCreated(@PathVariable("bizId") Long bizId, 
                             @PathVariable("uid") Long uid,
                             @PathVariable("pageNo") Integer pageNo,
                             @PathVariable("pageSize") Integer pageSize)
    {
        MeeResult meeResult = new MeeResult();
        try {
            PageVo pageVo = new PageVo();
            pageVo.setPageNo(pageNo);
            pageVo.setPageRows(pageSize);

            TodoPageResult result = todoService.getCreatedTodo(bizId, uid, pageVo);
            meeResult.setData(result);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);

        } catch (MeeException meeEx) {
            logger.error("getTodo Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("getTodo Error bizId = {}", bizId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    };

    @RequestMapping(value = "/finish/{todoId}", method = RequestMethod.PUT)
    public MeeResult finishTodo(@PathVariable("todoId") Long todoId){
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = todoService.finishiTodo(todoId);
            if(flag)
                meeResult.setStatusCodeDes(StatusCode.SUCCESS);
            else 
                meeResult.setStatusCodeDes(StatusCode.FAIL);
        
        } catch (MeeException meeEx) {
            logger.error("setTodo Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("setTodo Error todoId = {}", todoId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    };

    @RequestMapping(value = "/unset/{todoId}", method = RequestMethod.PUT)
    public MeeResult unSetTodo(@PathVariable("todoId") Long todoId){
        MeeResult meeResult = new MeeResult();
        try {
            boolean flag = todoService.unSetTodo(todoId);
            if(flag)
                meeResult.setStatusCodeDes(StatusCode.SUCCESS);
            else 
                meeResult.setStatusCodeDes(StatusCode.FAIL);

        } catch (MeeException meeEx) {
            logger.error("setTodo Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("setTodo Error todoId = {}", todoId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    };


    @RequestMapping(value = "/count/{bizId}/{userId}", method = RequestMethod.GET)
    public MeeResult countTodo(@PathVariable("bizId") Long bizId, @PathVariable("userId") Long userId){
        MeeResult meeResult = new MeeResult();
        try {
            Integer number = todoService.getTodoNumber(bizId, userId);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
            meeResult.setData(number);

        } catch (MeeException meeEx) {
            logger.error("countTodo Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("countTodo Error userId = {}", userId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    };

    @RequestMapping(value = "/createdcount/{bizId}/{userId}", method = RequestMethod.GET)
    public MeeResult countCreatedTodo(@PathVariable("bizId") Long bizId, @PathVariable("userId") Long userId){
        MeeResult meeResult = new MeeResult();
        try {
            Integer number = todoService.getCreatedNumber(bizId, userId);
            meeResult.setStatusCodeDes(StatusCode.SUCCESS);
            meeResult.setData(number);

        } catch (MeeException meeEx) {
            logger.error("countTodo Error", meeEx);
            meeResult.setStatusCodeDes(meeEx.getStatusCode());

        } catch (Exception ex) {
            logger.error("countTodo Error userId = {}", userId, ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        } 

        return meeResult;
    };
    
}
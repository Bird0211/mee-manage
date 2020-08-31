package com.mee.manage.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.Biz;

/**
 * IBizService
 */
public interface IBizService extends IService<Biz> {

    List<Biz> getAllBiz();

    Biz getBiz(Long bizId);
}
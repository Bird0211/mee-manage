package com.mee.manage.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IBizMapper;
import com.mee.manage.po.Biz;
import com.mee.manage.service.IBizService;

import org.springframework.stereotype.Service;

/**
 * BizServiceImpl
 */
@Service
public class BizServiceImpl extends ServiceImpl<IBizMapper, Biz> implements IBizService {

    @Override
    public List<Biz> getAllBiz() {
        return list();
    }

    @Override
    public Biz getBiz(Long bizId) {
        return getById(bizId);
    }

    
}
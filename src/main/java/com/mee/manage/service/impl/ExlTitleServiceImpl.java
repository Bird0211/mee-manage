package com.mee.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IExlTitleMapper;
import com.mee.manage.po.ExlTitle;
import com.mee.manage.service.IExlTitleService;
import com.mee.manage.vo.ExlTitleVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ExlTitleServiceImpl extends ServiceImpl<IExlTitleMapper, ExlTitle> implements IExlTitleService {
    @Override
    public ExlTitle getExlTitle(String businessName) {
        if(StringUtils.isEmpty(businessName))
            return null;

        QueryWrapper<ExlTitle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("business_name",businessName);

        return getOne(queryWrapper);
    }

    @Override
    public ExlTitleVo getExlTitleByName(String businessName) {
        ExlTitle exlTitle = getExlTitle(businessName);
        ExlTitleVo exlTitleVo = null;
        if(exlTitle != null) {
            exlTitleVo = new ExlTitleVo(exlTitle);
        }
        return exlTitleVo;
    }

    @Override
    public boolean updateExlTitle(ExlTitleVo exlTitle) {
        if(exlTitle == null || !checkParams(exlTitle.toExlTitle()))
            return false;


        UpdateWrapper<ExlTitle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("business_name",exlTitle.getBusinessName());
        updateWrapper.set("addr",exlTitle.getAddr());
        updateWrapper.set("name",exlTitle.getName());
        updateWrapper.set("order_no",exlTitle.getOrderNo());
        updateWrapper.set("phone",exlTitle.getPhone());
        updateWrapper.set("product_name",exlTitle.getProductName());
        updateWrapper.set("num",exlTitle.getNum());
        updateWrapper.set("express",exlTitle.getExpress());
        updateWrapper.set("id_no",exlTitle.getIdNo());
        updateWrapper.set("sku",exlTitle.getSku());



        return update(updateWrapper);
    }

    @Override
    public boolean addExlTitle(ExlTitleVo exlTitle) {

        if(exlTitle == null || !checkParams(exlTitle.toExlTitle()))
            return false;

        return save(exlTitle.toExlTitle());
    }

    private boolean checkParams(ExlTitle exlTitle){
        if(exlTitle == null ||
                StringUtils.isEmpty(exlTitle.getBusinessName()) ||
                StringUtils.isEmpty(exlTitle.getName()) ||
                StringUtils.isEmpty(exlTitle.getAddr()) ||
                StringUtils.isEmpty(exlTitle.getExpress()) ||
                StringUtils.isEmpty(exlTitle.getNum()) ||
                StringUtils.isEmpty(exlTitle.getOrderNo()) ||
                StringUtils.isEmpty(exlTitle.getPhone()) ||
                StringUtils.isEmpty(exlTitle.getProductName()) ||
                StringUtils.isEmpty(exlTitle.getSku())
        )
            return false;

        return true;
    }
}

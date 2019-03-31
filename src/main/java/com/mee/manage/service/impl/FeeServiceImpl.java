package com.mee.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IFeeMapper;
import com.mee.manage.po.Fee;
import com.mee.manage.service.IFeeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeeServiceImpl extends ServiceImpl<IFeeMapper, Fee> implements IFeeService {
    @Override
    public List<Fee> getFeeList(int userType) {
        if(userType < 1)
            return null;

        QueryWrapper<Fee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_type",userType);

        return list(queryWrapper);
    }
}

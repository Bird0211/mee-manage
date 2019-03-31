package com.mee.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.UserMapper;
import com.mee.manage.po.User;
import com.mee.manage.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User getUserByName(String name) {
        if(StringUtils.isEmpty(name))
            return null;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        User user = getOne(queryWrapper);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        if(userId == null)
            return null;

        User user = getById(userId);
        return user;
    }

}

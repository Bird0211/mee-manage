package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.User;

public interface IUserService extends IService<User> {


    User getUserByName(String name);

    User getUserById(Long userId);
}

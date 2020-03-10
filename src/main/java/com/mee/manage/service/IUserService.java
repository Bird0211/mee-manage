package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.User;
import com.mee.manage.vo.Yiyun.YiyunUserData;

import java.util.List;

public interface IUserService extends IService<User> {


    User getUserByName(String name);

    User getUserById(Long userId);

    List<YiyunUserData> getAllYiYunUser(String bizId);

    YiyunUserData getYiyunUser(String bizId, String userId);

}

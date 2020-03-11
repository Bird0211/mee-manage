package com.mee.manage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IUserTypeMapper;
import com.mee.manage.po.UserType;
import com.mee.manage.service.IUserTypeService;

import org.springframework.stereotype.Service;

@Service
public class UserTypeServiceImpl extends ServiceImpl<IUserTypeMapper, UserType> implements IUserTypeService {
}

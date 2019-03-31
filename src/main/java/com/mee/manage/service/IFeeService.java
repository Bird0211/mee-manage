package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.Fee;

import java.util.List;

public interface IFeeService extends IService<Fee> {


    List<Fee> getFeeList(int userType);

}

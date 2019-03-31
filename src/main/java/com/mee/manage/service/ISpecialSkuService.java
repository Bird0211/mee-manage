package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.SpecialSku;

import java.util.List;


public interface ISpecialSkuService extends IService<SpecialSku> {

    List<Long> getSpecialSkuByUserId(Long userId,Integer batchId);



}

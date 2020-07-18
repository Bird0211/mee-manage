package com.mee.manage.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.DataTop;
import com.mee.manage.vo.Yiyun.YiyunTopProduct;

public interface IDataTopService extends IService<DataTop> {
    
    boolean saveDataTop(Integer bizId, List<YiyunTopProduct> datas);

    List<YiyunTopProduct> getTopProduct(Integer bizId);

}
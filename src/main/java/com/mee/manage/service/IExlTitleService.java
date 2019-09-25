package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.ExlTitle;
import com.mee.manage.vo.ExlTitleVo;

public interface IExlTitleService extends IService<ExlTitle> {

    ExlTitle getExlTitle(String businessName);

    ExlTitleVo getExlTitleByName(String businessName);

    boolean updateExlTitle(ExlTitleVo exlTitle);

    boolean addExlTitle(ExlTitleVo exlTitle);


}

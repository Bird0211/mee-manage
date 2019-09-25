package com.mee.manage.service.impl;

import com.mee.manage.service.IManageService;
import com.mee.manage.vo.ExlTitleVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ManageServiceImpl implements IManageService {
    @Override
    public ExlTitleVo getExlTitle(String name) {
        if(StringUtils.isEmpty(name))
            return null;



        return null;
    }
}

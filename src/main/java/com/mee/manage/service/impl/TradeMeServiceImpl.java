package com.mee.manage.service.impl;

import com.mee.manage.service.IConfigurationService;
import com.mee.manage.service.ITradeMeService;
import com.mee.manage.vo.MeeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeMeServiceImpl implements ITradeMeService {

    @Autowired
    IConfigurationService configurationService;

    @Override
    public MeeResult checkToken() {
        


        return null;
    }

    @Override
    public boolean requestToken(String token, String verifier) {
        return false;
    }
}

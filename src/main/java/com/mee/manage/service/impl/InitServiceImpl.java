package com.mee.manage.service.impl;

import com.mee.manage.service.IInitService;
import com.mee.manage.service.IJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class InitServiceImpl implements IInitService, ApplicationRunner {

    @Autowired
    IJob job;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        job.loadYiyunData();
        // job.loadTopOrderData();
    }
    
}
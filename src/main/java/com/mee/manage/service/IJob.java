package com.mee.manage.service;

public interface IJob {

    void loadYiyunData();

    void loadErrorData();
    
    void loadTopOrderData();

    void loadUggDelivery();

    void refreshWeimobToken();
}
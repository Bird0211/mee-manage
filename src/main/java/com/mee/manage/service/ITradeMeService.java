package com.mee.manage.service;

import com.mee.manage.vo.MeeResult;

public interface ITradeMeService {

    MeeResult checkToken();

    boolean requestToken(String token,String verifier);

}

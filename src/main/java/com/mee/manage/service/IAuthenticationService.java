package com.mee.manage.service;

import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.AuthenticationVo;

public interface IAuthenticationService {

    StatusCode checkAuth(AuthenticationVo auth);

    String getMeeToken(String bizId);
}

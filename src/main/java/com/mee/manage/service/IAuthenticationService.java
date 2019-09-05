package com.mee.manage.service;

import com.mee.manage.vo.AuthenticationVo;

public interface IAuthenticationService {

    boolean checkAuth(AuthenticationVo auth);
}

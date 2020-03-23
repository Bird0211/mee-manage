package com.mee.manage.service;
import com.mee.manage.vo.MenuVo;

import java.util.List;

public interface IUserMenuService {

    List<MenuVo> getMenuByUserId(Long userId, Long bizId);

}

package com.mee.manage.mapper;

import java.util.List;

import com.mee.manage.vo.MenuVo;

public interface IUserMenuMapper {

    List<MenuVo> getMenuByUser(Long bizId,Long userId);

}

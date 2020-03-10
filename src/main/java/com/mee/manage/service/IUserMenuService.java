package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.Menu;
import com.mee.manage.po.UserMenu;
import com.mee.manage.vo.MenuVo;

import java.util.List;

public interface IUserMenuService extends IService<UserMenu> {

    List<MenuVo> getMenuByUserId(Long userId);

    boolean addMenu(List<Menu> menus,Long userId);

    boolean removeMenu(List<Menu> menus,Long userId);

    boolean updateMenu(List<Menu> menus,Long userId);

}

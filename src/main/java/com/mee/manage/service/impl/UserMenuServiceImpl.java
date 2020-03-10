package com.mee.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mee.manage.mapper.IUserMenuMapper;
import com.mee.manage.po.Menu;
import com.mee.manage.po.UserMenu;
import com.mee.manage.service.IMenuService;
import com.mee.manage.service.IUserMenuService;
import com.mee.manage.vo.MenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMenuServiceImpl extends ServiceImpl<IUserMenuMapper, UserMenu> implements IUserMenuService {

    @Autowired
    IMenuService menuService;

    @Override
    public List<MenuVo> getMenuByUserId(Long userId) {
        if(userId == null)
            return null;

        QueryWrapper<UserMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);

        List<UserMenu> userMenus = list(queryWrapper);
        if(userMenus == null)
            return null;

        List<Long> menuIds = Lists.newArrayList();
        for(UserMenu userMenu : userMenus) {
            menuIds.add(userMenu.getMenuId());
        }
        List<MenuVo> menus = menuService.getMenuByIds(menuIds);

        return menus;
    }

    @Override
    public boolean addMenu(List<Menu> menus, Long userId) {
        return false;
    }

    @Override
    public boolean removeMenu(List<Menu> menus, Long userId) {
        return false;
    }

    @Override
    public boolean updateMenu(List<Menu> menus, Long userId) {
        return false;
    }
}

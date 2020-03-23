package com.mee.manage.service.impl;

import com.mee.manage.po.Role;
import com.mee.manage.po.RoleMenu;
import com.mee.manage.po.RoleUser;
import com.mee.manage.service.IMenuService;
import com.mee.manage.service.IRoleMenuService;
import com.mee.manage.service.IRoleService;
import com.mee.manage.service.IRoleUserService;
import com.mee.manage.service.IUserMenuService;
import com.mee.manage.vo.MenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMenuServiceImpl implements IUserMenuService {

    @Autowired
    IRoleUserService roleUserService;

    @Autowired
    IRoleMenuService roleMenuService;

    @Autowired
    IRoleService roleService;

    @Autowired
    IMenuService menuService;

    @Override
    public List<MenuVo> getMenuByUserId(Long userId, Long bizId) {
        if (userId == null)
            return null;

        List<RoleUser> roleUsers = roleUserService.getRoleUserByUserId(userId);
        if (roleUsers == null || roleUsers.isEmpty())
            return null;

        List<Long> roleIds = roleUsers.stream().map(item -> item.getRoleId()).collect(Collectors.toList());
        List<Role> roles = roleService.getRoleById(roleIds, bizId);
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        roleIds = roles.stream().map(item -> item.getId()).collect(Collectors.toList());
        List<RoleMenu> roleMenus = roleMenuService.getRoleMenuByRoles(roleIds);
        if (roleMenus == null || roleMenus.isEmpty())
            return null;

        List<Long> menuIds = roleMenus.stream().map(item -> item.getMenuId()).collect(Collectors.toList());

        List<MenuVo> menus = menuService.getMenuVoByIds(menuIds);

        return menus;
    }

}

package com.mee.manage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IRoleMenuMapper;
import com.mee.manage.po.RoleMenu;
import com.mee.manage.service.IMenuService;
import com.mee.manage.service.IRoleMenuService;
import com.mee.manage.vo.MenuVo;
import com.mee.manage.vo.RoleMenusVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RoleMenuServiceImpl
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<IRoleMenuMapper, RoleMenu> implements IRoleMenuService {

    @Autowired
    IMenuService menuService;

    @Override
    public RoleMenu addRoleMenu(RoleMenu roleMenu) {
        boolean result = save(roleMenu);
        if (result)
            return roleMenu;
        else
            return null;
    }

    @Override
    public List<RoleMenu> getRoleMenuByRoleId(Long roleId) {
        QueryWrapper<RoleMenu> qWrapper = new QueryWrapper<>();
        qWrapper.eq("role_id", roleId);

        return list(qWrapper);
    }

    @Override
    public boolean delRole(Long roleId) {
        QueryWrapper<RoleMenu> qWrapper = new QueryWrapper<>();
        qWrapper.eq("role_id", roleId);
        return remove(qWrapper);
    }

    @Override
    public boolean delRoleMenu(Long id) {
        return removeById(id);
    }

    @Override
    public List<MenuVo> getRoleMenu(Long roleId) {
        List<RoleMenu> roleMenus = getRoleMenuByRoleId(roleId);
        List<MenuVo> menus = null;
        if (roleMenus != null && !roleMenus.isEmpty()) {
            List<Long> menuIds = roleMenus.stream().map(item -> item.getMenuId()).collect(Collectors.toList());
            menus = menuService.getMenuVoByIds(menuIds);
        }
        return menus;
    }

    @Override
    public List<RoleMenu> getRoleMenuByRoles(List<Long> roleIds) {
        QueryWrapper<RoleMenu> qWrapper = new QueryWrapper<>();
        qWrapper.in("role_id", roleIds);

        return list(qWrapper);
    }

    @Override
    public boolean updateRoleMenus(RoleMenusVo roleMenuIds) {
        List<RoleMenu> roleMenu = getRoleMenuByRoleId(roleMenuIds.getRoleId());
        List<Long> addRoleMenus = getAddRoleMenu(roleMenu.stream().map(item -> item.getMenuId()).collect(Collectors.toList()), roleMenuIds.getMenuIds());
        boolean flag = true;
        
        if(addRoleMenus != null && !addRoleMenus.isEmpty()) 
            saveBatch(addRoleMenus.stream().map(item -> new RoleMenu(roleMenuIds.getRoleId(),item)).collect(Collectors.toList()));
        if(flag) {
            List<RoleMenu> delRoleMenus = getDelRoleMenu(roleMenu, roleMenuIds.getMenuIds());
            if(delRoleMenus != null && !delRoleMenus.isEmpty()) 
                flag = removeByIds(delRoleMenus.stream().map(item -> item.getId()).collect(Collectors.toList()));
        }

        return flag;
    }


    private List<Long> getAddRoleMenu(List<Long> roleMenusIds, List<Long> menuIds) {
        if(roleMenusIds == null || roleMenusIds.isEmpty())
            return menuIds;

        return menuIds.stream().filter(item -> !roleMenusIds.contains(item)).collect(Collectors.toList());
    }

    private List<RoleMenu> getDelRoleMenu(List<RoleMenu> roleMenus, List<Long> menuIds) {
        if(menuIds == null || menuIds.isEmpty())
            return roleMenus;
        
        return roleMenus.stream().filter(item -> !menuIds.contains(item.getMenuId())).collect(Collectors.toList());
    }

    
}
package com.mee.manage.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.RoleMenu;
import com.mee.manage.vo.MenuVo;
import com.mee.manage.vo.RoleMenusVo;

/**
 * IMenuRoleService
 */
public interface IRoleMenuService extends IService<RoleMenu> {

    public RoleMenu addRoleMenu(RoleMenu roleMenu);
   
    public List<RoleMenu> getRoleMenuByRoleId(Long roleId); 

    public List<RoleMenu> getRoleMenuByRoles(List<Long> roleIds);

    public List<MenuVo> getRoleMenu(Long roleId);

    public boolean delRole(Long roleId);

    public boolean delRoleMenu(Long id);

    public boolean updateRoleMenus(RoleMenusVo roleMenus);

}
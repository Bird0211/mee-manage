package com.mee.manage.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.RoleUser;
import com.mee.manage.vo.RoleUserVo;

/**
 * IRoleUserService
 */
public interface IRoleUserService extends IService<RoleUser> {

    List<RoleUser> getRoleUseByRoleId(Long roleId);

    List<RoleUser> getRoleUserByUserId(Long userId);
    
    RoleUser addRoleUser(RoleUser roleUser);

    boolean removeRoleUserByRoleId(Long roleId);

    boolean removeRoleUserById(Long roleUserId);

    boolean updateRoleUser(RoleUserVo roleUserVo);


}
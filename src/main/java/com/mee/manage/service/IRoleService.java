package com.mee.manage.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.Role;
import com.mee.manage.vo.RoleVo;

/**
 * IRoleService
 */

public interface IRoleService extends IService<Role> {

    List<RoleVo> getRoleByBiz(Long bizId);

    List<Role> getRoleById(List<Long> ids,Long bizId);

    RoleVo addRole(RoleVo role);
    
    boolean editRole(Role role);

    boolean delRole(Long roleId);
    
    
}
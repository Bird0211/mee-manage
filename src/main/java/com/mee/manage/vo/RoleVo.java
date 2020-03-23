
package com.mee.manage.vo;

import com.mee.manage.po.Role;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RoleVo
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RoleVo extends Role{

    private Integer roleType;

    RoleVo() {

    }

    public RoleVo(Role role) {
        setId(role.getId());
        setBizId(role.getBizId());
        setRoleName(role.getRoleName());
        setRoleType(role.getBizId() == 0 ? 0 : 1);
    }

    public Role toRole() {
        Role role = new Role();
        role.setBizId(getRoleType() == 0 ? 0L : getBizId());
        role.setId(getId());
        role.setRoleName(getRoleName());
        return role;
    }
    
}
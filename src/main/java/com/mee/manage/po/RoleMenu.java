package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mee.JsonLongSerializer;

import lombok.Data;

/**
 * RoleMenu
 */
@Data
@TableName("t_mee_role_menu")
public class RoleMenu {
    
    RoleMenu() {

    }

    public RoleMenu(Long roleId, Long menuId) {
        setRoleId(roleId);
        setMenuId(menuId);
    }

    @TableId
    @JsonSerialize(using = JsonLongSerializer.class )
    Long id;

    @JsonSerialize(using = JsonLongSerializer.class )
    Long roleId;

    @JsonSerialize(using = JsonLongSerializer.class )
    Long menuId;
    
}
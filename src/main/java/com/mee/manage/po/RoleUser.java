package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mee.JsonLongSerializer;

import lombok.Data;

/**
 * RoleUser
 */
@Data
@TableName("t_mee_role_user")
public class RoleUser {

    RoleUser() {

    }

    public RoleUser(Long roleId, Long userId){
        setRoleId(roleId);
        setUserId(userId);
    }

    @TableId
    @JsonSerialize(using = JsonLongSerializer.class )
    Long id;

    @JsonSerialize(using = JsonLongSerializer.class )
    Long roleId;

    @JsonSerialize(using = JsonLongSerializer.class )
    Long userId;
}
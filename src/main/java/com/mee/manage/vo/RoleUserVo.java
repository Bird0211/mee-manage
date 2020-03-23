package com.mee.manage.vo;

import java.util.List;

import lombok.Data;

/**
 * RoleUserVo
 */
@Data
public class RoleUserVo {

    Long roleId;

    List<Long> userIds;
    
}
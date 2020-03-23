package com.mee.manage.vo;

import java.util.List;

import lombok.Data;

/**
 * RoleMenusVo
 */
@Data
public class RoleMenusVo {

    Long RoleId;

    List<Long> menuIds;
}
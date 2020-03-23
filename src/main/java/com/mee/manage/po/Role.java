
package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mee.JsonLongSerializer;

import lombok.Data;

/**
 * Role
 */
@Data
@TableName("t_mee_role")
public class Role {

    @TableId
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long id;
    
    private String roleName;

    @JsonSerialize(using = JsonLongSerializer.class )
    private Long bizId;
}
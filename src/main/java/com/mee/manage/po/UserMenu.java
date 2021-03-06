package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mee.JsonLongSerializer;

import lombok.Data;

@Data
@TableName("t_mee_user_menu")
public class UserMenu {

    @TableId
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long id;

    @JsonSerialize(using = JsonLongSerializer.class )
    private Long userId;

    @JsonSerialize(using = JsonLongSerializer.class )
    private Long menuId;

}

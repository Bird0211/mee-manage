package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_mee_user_menu")
public class UserMenu {

    @TableId
    private Long id;

    private Long userId;

    private Long menuId;

}

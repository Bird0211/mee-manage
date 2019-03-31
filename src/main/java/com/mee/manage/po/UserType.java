package com.mee.manage.po;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_mee_manage_user_type")
public class UserType {

    private Long id;

    private String name;

    private String remark;

}

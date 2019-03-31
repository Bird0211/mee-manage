package com.mee.manage.po;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_manage_user")
public class User {

    private Long id;

    private String name;

    private Integer type;

}

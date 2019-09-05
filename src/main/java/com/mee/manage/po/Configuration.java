package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_mee_configuration")
public class Configuration {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("`key`")
    private String key;

    @TableField("`value`")
    private String value;

    private Date expir;

}

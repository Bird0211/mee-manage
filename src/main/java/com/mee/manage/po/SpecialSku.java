package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("t_manage_special_sku")
public class SpecialSku {

    @TableId
    private Long id;

    private Long sku;

    private Long userId;

    private String userName;

    private Integer bath;



}

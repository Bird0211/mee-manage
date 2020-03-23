package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * Biz
 */
@Data
@TableName("t_mee_biz")
public class Biz {

    @TableId
    Integer id;

    String name;

    String token;
    
}
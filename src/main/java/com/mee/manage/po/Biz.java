package com.mee.manage.po;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * Biz
 */
@Data
@TableName("t_mee_biz")
public class Biz {

    @TableId
    Long id;

    String name;

    String token;

    Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date expireDate;
    
}
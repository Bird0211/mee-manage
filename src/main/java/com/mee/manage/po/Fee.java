package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_manage_fee")
public class Fee {
    private Long id;
    private String name;
    private BigDecimal fee;
    private Integer userType;
    private Integer feeType;

    @TableField("`key`")
    private String key;
    @TableField("`condition`")
    private String condition;
    private String value;


}

package com.mee.manage.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_manage_exl_title")
public class ExlTitle {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String businessName;

    private String orderNo;

    private String name;

    private String phone;

    private String addr;

    private String productName;

    private String num;

    private String express;

    private String idNo;

    private String sku;
}

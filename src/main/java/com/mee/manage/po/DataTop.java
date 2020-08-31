package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@TableName("t_mee_data_top")
@Data
public class DataTop {

    @TableId(type = IdType.ID_WORKER)
    Long id;

    Long bizId;
    
    String sku;

    Integer number;
}
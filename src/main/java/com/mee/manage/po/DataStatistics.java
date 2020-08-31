package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("t_mee_data_statistics")
public class DataStatistics {

    @TableId
    Long bizId;
    
    Long noShip;

    Long error;
}
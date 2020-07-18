package com.mee.manage.po;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("t_mee_data_sales")
public class DataSales {

    @TableId(type = IdType.ID_WORKER)
    Long id;

    Long bizId;

    Long totalNumber;

    BigDecimal totalPrice;

    Date salesDate;
}


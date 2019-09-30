package com.mee.manage.po;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_mee_weimob_order")
public class WeimobOrder {

    @TableId
    Long id;

    Long sku;

    Long goodId;

    BigDecimal lastCostPrice;

    BigDecimal lastSalesPrice;

    Date updateDate;

}

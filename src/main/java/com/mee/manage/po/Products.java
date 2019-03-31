package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_mee_products")
public class Products {

    @TableId
    private Long id;

    private Long sku;

    private String name;

    private String overseaName;

    private Integer categoryId;

    private BigDecimal costPrice;

    private BigDecimal retailPrice;

    private BigDecimal overseaCostPrice;

    private BigDecimal overseaRetailPrice;

    private String brand;

    private Integer weight;

    private Date createTime;

    private Date updateTime;

    /**
     * 0:正常
     * 1:删除
     */
    private Integer state;

}

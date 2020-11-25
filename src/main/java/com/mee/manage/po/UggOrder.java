package com.mee.manage.po;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mee.JsonLongSerializer;

import lombok.Data;

@Data
@TableName("t_mee_ugg_order")
public class UggOrder {
    
    @TableId(type = IdType.ID_WORKER)
    @JsonSerialize(using = JsonLongSerializer.class )
    Long id;

    String extId;

    String productName;

    Integer qty;

    Date createTime;

    Integer bizId;

    String resource;

    BigDecimal price;

    BigDecimal settlementPrice;

    Integer status;

    String batchId;

    String expressId;

    String expressName;

    String imageUrl;

    String receiveName;

    String receivePhone;

    String receiveAddress;

    Long productSku;

}

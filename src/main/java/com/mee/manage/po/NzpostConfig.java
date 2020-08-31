package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mee.JsonLongSerializer;

import lombok.Data;

@Data
@TableName("t_mee_nzpost_config")
public class NzpostConfig {

    @TableId(type = IdType.ID_WORKER)
    @JsonSerialize(using = JsonLongSerializer.class )
    Long id;

    Long bizId;

    String reference;

    String companyName;

    String name;

    String phone;

    String email;

    String street;

    String suburb;

    String city;

    String postcode;

    String countryCode;


    
}
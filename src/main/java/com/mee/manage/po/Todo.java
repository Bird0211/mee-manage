package com.mee.manage.po;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mee.JsonLongSerializer;

import lombok.Data;

@Data
@TableName("t_mee_todo")
public class Todo {

    @TableId(type = IdType.ID_WORKER)
    @JsonSerialize(using = JsonLongSerializer.class )
    Long id;

    String title;

    Date createDate;

    Long bizId;

    Long uid;

    Long createUid;

    //0: 未完成； 1: 已完成
    Integer status;
    
}
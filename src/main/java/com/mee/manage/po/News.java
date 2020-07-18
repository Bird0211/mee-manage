package com.mee.manage.po;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mee.JsonLongSerializer;

import lombok.Data;

@Data
@TableName("t_mee_news")
public class News {
    
    @TableId(type = IdType.ID_WORKER)
    @JsonSerialize(using = JsonLongSerializer.class )
    Long id;

    String title;

    String content;

    Date updateDate;

    Integer type;

}
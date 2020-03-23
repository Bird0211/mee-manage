package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mee.JsonLongSerializer;

import lombok.Data;

/**
 * BizMenu
 */
@Data
@TableName("t_mee_biz_menu")
public class BizMenu {

    BizMenu(){

    }

    public BizMenu(Long bizId, Long menuId){
        this.bizId = bizId;
        this.menuId = menuId;
    }

    @TableId
    @JsonSerialize(using = JsonLongSerializer.class )
    Long id;

    @JsonSerialize(using = JsonLongSerializer.class )
    Long bizId;

    @JsonSerialize(using = JsonLongSerializer.class )
    Long menuId;
    
}
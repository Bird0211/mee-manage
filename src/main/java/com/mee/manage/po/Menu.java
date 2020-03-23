package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mee.JsonLongSerializer;

import lombok.Data;

@Data
@TableName("t_mee_menu")
public class Menu implements Comparable<Menu> {

    @TableId
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long id;

    private String title;

    private String description;

    private String type;

    private String url;

    private String level;

    @JsonSerialize(using = JsonLongSerializer.class )
    private Long parentId;

    private Integer sort;

    private String icon;

    private String iconColor;


    @Override
    public int compareTo(Menu o) {
        if(this.getSort() < o.getSort())
            return -1;
        else if(this.getSort() > o.getSort()) {
            return 1;
        }
        return 0;
    }
}

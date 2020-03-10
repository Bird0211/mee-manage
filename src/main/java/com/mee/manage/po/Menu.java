package com.mee.manage.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mee.manage.vo.WordsVo;
import lombok.Data;

@Data
@TableName("t_mee_menu")
public class Menu implements Comparable<Menu> {

    @TableId
    private Long id;

    private String title;

    private String description;

    private String type;

    private String url;

    private String level;

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

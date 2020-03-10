package com.mee.manage.vo;

import com.mee.manage.po.Menu;
import lombok.Data;

import java.util.List;

@Data
public class MenuVo extends Menu {

    public MenuVo(Menu menu) {
        this.setId(menu.getId());
        this.setDescription(menu.getDescription());
        this.setLevel(menu.getLevel());
        this.setParentId(menu.getParentId());
        this.setSort(menu.getSort());
        this.setTitle(menu.getTitle());
        this.setType(menu.getType());
        this.setUrl(menu.getUrl());
        this.setIcon(menu.getIcon());
        this.setIconColor(menu.getIconColor());
    }

    private List<MenuVo> subMenu;

}

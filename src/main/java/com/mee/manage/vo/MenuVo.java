package com.mee.manage.vo;

import com.mee.manage.po.Menu;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class MenuVo extends Menu {

    private List<MenuVo> subMenu;

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

}

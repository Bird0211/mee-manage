package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.Menu;
import com.mee.manage.vo.MenuVo;

import java.util.List;

public interface IMenuService extends IService<Menu> {

    List<MenuVo> getAllMenu();

    List<MenuVo> getSubMenu(Long pid);

    List<MenuVo> getMenuByIds(List<Long> menuIds);

    boolean updateMenu(Menu menu);

    boolean delMenu(Long id);
}

package com.mee.manage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mee.manage.mapper.IMenuMapper;
import com.mee.manage.po.Menu;
import com.mee.manage.service.IMenuService;
import com.mee.manage.vo.MenuVo;

import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends ServiceImpl<IMenuMapper, Menu> implements IMenuService {
    @Override
    public List<MenuVo> getAllMenu() {
        List<Menu> menus = list();
        return getMenuVos(menus,0L);
    }

    @Override
    public List<MenuVo> getSubMenu(Long pid) {
        if(pid == null)
            return null;

        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",pid);
        List<Menu> menus = list(queryWrapper);
        return getMenuVos(menus,pid);
    }

    @Override
    public List<MenuVo> getMenuByIds(List<Long> menuIds) {
        if(menuIds == null || menuIds.isEmpty())
            return null;

        QueryWrapper<Menu> queryWrapper = new QueryWrapper<Menu>();
        queryWrapper.in("id", menuIds);

        List<Menu> menus = list(queryWrapper);
        return getMenuVos(menus,0L);
    }

    @Override
    public boolean updateMenu(Menu menu) {
        if(menu == null)
            return false;

        return updateById(menu);
    }

    @Override
    public boolean delMenu(Long id) {
        return removeById(id);
    }

    List<MenuVo> getMenuVos(List<Menu> menus, Long pid) {
        if(menus == null)
            return null;

        List<Menu> parentParen =
                menus.stream().
                        filter(menu -> menu.getParentId() == pid).
                        sorted()
                        .collect(Collectors.toList());

        if(parentParen == null || parentParen.isEmpty())
            return null;

        List<MenuVo> list = Lists.newArrayList();

        parentParen.forEach(menu -> {
            MenuVo menuVo = new MenuVo(menu);
            menuVo.setSubMenu(getMenuVos(menus,menu.getId()));
            list.add(menuVo);
        });
        return list;
    }

}

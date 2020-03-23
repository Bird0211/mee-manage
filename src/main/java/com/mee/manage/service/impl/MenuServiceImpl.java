package com.mee.manage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mee.manage.mapper.IMenuMapper;
import com.mee.manage.po.BizMenu;
import com.mee.manage.po.Menu;
import com.mee.manage.service.IBizMenuService;
import com.mee.manage.service.IMenuService;
import com.mee.manage.vo.MenuVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MenuServiceImpl extends ServiceImpl<IMenuMapper, Menu> implements IMenuService {

    @Autowired
    IBizMenuService bizMenuService;

    @Override
    public List<MenuVo> getAllMenu(String bizId) {
        List<Menu> menus = null;
        if(bizId == null || bizId.equals("all"))
            menus = list();
        else 
            menus = getBizMenu(bizId);
        return getMenuVos(menus,0L);
    }

    private List<Menu> getBizMenu(String bizId) {
        if(bizId == null || StringUtils.isEmpty(bizId))
            return null;
        
        List<BizMenu> bizMenus = bizMenuService.getBizMenuByBizId(Long.parseLong(bizId));
        if(bizMenus == null || bizMenus.isEmpty()) {
            return null;
        }
        List<Long> menuIds = bizMenus.stream().map( item -> item.getMenuId()).collect(Collectors.toList());
        List<Menu> menus = getMenuByIds(menuIds);
        return menus;
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
    public List<MenuVo> getMenuVoByIds(List<Long> menuIds) {
        List<Menu> menus = getMenuByIds(menuIds);
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
                        filter(menu -> menu.getParentId().compareTo(pid) == 0).
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

    @Override
    public Menu insertMenu(Menu menu) {
        if(menu == null)
            return null;

        boolean isInsert = save(menu);
        if(isInsert) {
            return menu;
        } else
            return null;
    }

    @Override
    public List<Menu> getMenuByIds(List<Long> menuIds) {
        if(menuIds == null || menuIds.isEmpty())
        return null;

        QueryWrapper<Menu> queryWrapper = new QueryWrapper<Menu>();
        queryWrapper.in("id", menuIds);

        List<Menu> menus = list(queryWrapper);
        return menus;
    }

}

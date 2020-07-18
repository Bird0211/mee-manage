package com.mee.manage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mee.manage.config.Config;
import com.mee.manage.mapper.IMenuMapper;
import com.mee.manage.po.BizMenu;
import com.mee.manage.po.Menu;
import com.mee.manage.service.IBizMenuService;
import com.mee.manage.service.IConfigurationService;
import com.mee.manage.service.IMenuService;
import com.mee.manage.vo.MenuVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MenuServiceImpl extends ServiceImpl<IMenuMapper, Menu> implements IMenuService {

    @Autowired
    IBizMenuService bizMenuService;

    @Autowired
    IConfigurationService configService;

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
    public List<MenuVo> getSubMenuVoByIds(List<Long> menuIds, Long pid) {
        if(pid == null)
            return null;

        List<MenuVo> menuVos = null;

        Menu menu = getById(pid);
        if(menu != null) {            
            QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parent_id",pid);
            queryWrapper.in("id", menuIds);
            List<Menu> menus = list(queryWrapper);
            if(menus != null && menus.size() > 0) {
                menuVos = Lists.newArrayList();
                MenuVo mVo = new MenuVo(menu);
                mVo.setSubMenu(getMenuVos(menus,pid));
                menuVos.add(mVo);
            }
        }
        return menuVos;
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

    @Override
    public List<MenuVo> getMenuBiz(List<Long> menuIds, Long bizId) {
        List<Long> menus = getMenuIdBiz(menuIds,bizId);
        return getMenuVoByIds(menus);
    }

    @Override
    public List<Long> getMenuIdBiz(List<Long> menuIds, Long bizId) {
        if(menuIds == null || menuIds.isEmpty())
            return null;
        List<BizMenu> bizMenus = bizMenuService.getBizMenuByBizId(bizId);
        if(bizMenus == null || bizMenus.isEmpty()) {
            return null;
        }

        List<Long> menus = bizMenus.stream().map(item -> item.getMenuId()).filter(item -> menuIds.indexOf(item) >=0 ).
                collect(Collectors.toList());
        
        return menus;
    }

    @Override
    public List<Menu> getOrderFlowMenu(Long bizId) {
        String value = configService.getValue(Config.ORDER_FLOW_MENU);
        if(value == null) {
            return null;
        }

        String [] menuIds = value.split(",");
        List<Long> menuLongs = Lists.newArrayList();

        for(int i = 0; i < menuIds.length ; i++) {
            menuLongs.add(Long.parseLong(menuIds[i]));
        }

        List<Menu> menus = getMenuByIds(menuLongs);
        List<Menu> sortMenus = null;
        if(menus != null) {
            sortMenus = Lists.newArrayList();
            for(String menuId: menuIds) {
                List<Menu> filterMenu = menus.stream().filter(item -> 
                    item.getId().compareTo(Long.parseLong(menuId)) == 0
                    ).collect(Collectors.toList());
                
                if(filterMenu != null) {
                    sortMenus.add(filterMenu.get(0));
                }
            }
        }
        return sortMenus;
    }


}

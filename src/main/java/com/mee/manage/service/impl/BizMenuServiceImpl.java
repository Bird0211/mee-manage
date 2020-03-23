package com.mee.manage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IBizMenuMapper;
import com.mee.manage.po.BizMenu;
import com.mee.manage.service.IBizMenuService;
import com.mee.manage.vo.EditBizMenuVo;

import org.springframework.stereotype.Service;

/**
 * BizMenuServiceImpl
 */
@Service
public class BizMenuServiceImpl extends ServiceImpl<IBizMenuMapper, BizMenu> implements IBizMenuService {

    @Override
    public List<BizMenu> getBizMenuByBizId(Long bizId) {
        QueryWrapper<BizMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("biz_id", bizId);

        return list(queryWrapper);
    }

    @Override
    public BizMenu addBizMenu(BizMenu bizMenu) {
        if (save(bizMenu)) {
            return bizMenu;
        } else {
            return null;
        }
    }

    @Override
    public boolean removeBizMenu(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updateBizMenu(EditBizMenuVo bizMenuVo) {
        Long bizId = bizMenuVo.getBizId();
        List<BizMenu> bizMenus = getBizMenuByBizId(bizId);
        List<Long> addBizMenus = getAddBizMenu(bizMenus.stream().map(item -> item.getMenuId()).collect(Collectors.toList()), bizMenuVo.getMenuId());
        boolean flag = true;
        if(addBizMenus != null && !addBizMenus.isEmpty()) 
            saveBatch(addBizMenus.stream().map(item -> new BizMenu(bizId,item)).collect(Collectors.toList()));
        if(flag) {
            List<BizMenu> delBizMenus = getDelBizMenu(bizMenus, bizMenuVo.getMenuId());
            if(delBizMenus != null && !delBizMenus.isEmpty()) 
                flag = removeByIds(delBizMenus.stream().map(item -> item.getId()).collect(Collectors.toList()));
        }

        return flag;
    }

    private List<Long> getAddBizMenu(List<Long> bizMenusIds, List<Long> menuIds) {
        if(bizMenusIds == null || bizMenusIds.isEmpty())
            return menuIds;
        return menuIds.stream().filter(item -> !bizMenusIds.contains(item)).collect(Collectors.toList());
    }

    private List<BizMenu> getDelBizMenu(List<BizMenu> bizMenus, List<Long> menuIds) {
        if(menuIds == null || menuIds.isEmpty())
            return bizMenus;
        
        return bizMenus.stream().filter(item -> !menuIds.contains(item.getMenuId())).collect(Collectors.toList());
    }

    
}
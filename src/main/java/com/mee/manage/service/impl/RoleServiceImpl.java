
package com.mee.manage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IRoleMapper;
import com.mee.manage.po.Role;
import com.mee.manage.service.IRoleService;
import com.mee.manage.vo.RoleVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * RoleServiceImpl
 */
@Service
public class RoleServiceImpl extends ServiceImpl<IRoleMapper, Role> implements IRoleService {

    protected static final Logger logger = LoggerFactory.getLogger(IRoleService.class);

    @Override
    public List<RoleVo> getRoleByBiz(Long bizId) {
        if(bizId == null)
            return null;

        QueryWrapper<Role> queryWrapper = new QueryWrapper<Role>();
        queryWrapper.in("biz_id", bizId, 0);

        List<Role> roles = list(queryWrapper);
        List<RoleVo> roleVos = null;
        if(roles != null) {
            roleVos = roles.stream().map( item -> 
                new RoleVo(item)
            ).collect(Collectors.toList());
        }
        return roleVos;
    }

    @Override
    public RoleVo addRole(RoleVo roleVo) {
        logger.info("Role = {}" ,roleVo);
        Role role = roleVo.toRole();
        boolean result = save(role);
        if(result) {
            roleVo.setId(role.getId());
            return roleVo;
        }
        else
            return null;
    }

    @Override
    public boolean editRole(Role role) {
        return updateById(role);
    }

    @Override
    public boolean delRole(Long roleId) {
        return removeById(roleId);
    }

    @Override
    public List<Role> getRoleById(List<Long> ids, Long bizId) {
        if(ids == null || ids.isEmpty())
            return null;

        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        if(bizId != null) {
            queryWrapper.in("biz_id", bizId, 0L);
        }
        return list(queryWrapper);
    }

    

    

    
}

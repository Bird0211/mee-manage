package com.mee.manage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IRoleUserMapper;
import com.mee.manage.po.RoleUser;
import com.mee.manage.service.IRoleUserService;
import com.mee.manage.vo.RoleUserVo;

import org.springframework.stereotype.Service;

/**
 * RoleUserServiceImpl
 */
@Service
public class RoleUserServiceImpl extends ServiceImpl<IRoleUserMapper, RoleUser> implements IRoleUserService {

    @Override
    public List<RoleUser> getRoleUseByRoleId(Long roleId) {
        QueryWrapper<RoleUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        return list(queryWrapper);
    }

    @Override
    public List<RoleUser> getRoleUserByUserId(Long userId) {
        QueryWrapper<RoleUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return list(queryWrapper);
    }

    @Override
    public RoleUser addRoleUser(RoleUser roleUser) {
        boolean result = save(roleUser);
        if (result)
            return roleUser;
        else
            return null;
    }

    @Override
    public boolean removeRoleUserByRoleId(Long roleId) {
        QueryWrapper<RoleUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        return remove(queryWrapper);
    }

    @Override
    public boolean removeRoleUserById(Long roleUserId) {
        return removeById(roleUserId);
    }

    @Override
    public boolean updateRoleUser(RoleUserVo roleUserVo) {
        List<RoleUser> roleUser = getRoleUseByRoleId(roleUserVo.getRoleId());
        List<Long> addRoleUsers = getAddRoleUser(roleUser.stream().map(item -> item.getUserId()).collect(Collectors.toList()), roleUserVo.getUserIds());
        boolean flag = true;
        
        if(addRoleUsers != null && !addRoleUsers.isEmpty()) 
            saveBatch(addRoleUsers.stream().map(item -> new RoleUser(roleUserVo.getRoleId(),item)).collect(Collectors.toList()));
        if(flag) {
            List<RoleUser> delRoleUsers = getDelRoleUser(roleUser, roleUserVo.getUserIds());
            if(delRoleUsers != null && !delRoleUsers.isEmpty()) 
                flag = removeByIds(delRoleUsers.stream().map(item -> item.getId()).collect(Collectors.toList()));
        }

        return flag;
    }
    

    private List<Long> getAddRoleUser(List<Long> roleUserIds, List<Long> userIds) {
        if(roleUserIds == null || roleUserIds.isEmpty())
            return userIds;

        return userIds.stream().filter(item -> !roleUserIds.contains(item)).collect(Collectors.toList());
    }

    private List<RoleUser> getDelRoleUser(List<RoleUser> roleUser, List<Long> userIds) {
        if(userIds == null || userIds.isEmpty())
            return roleUser;
        
        return roleUser.stream().filter(item -> !userIds.contains(item.getUserId())).collect(Collectors.toList());
    }

}
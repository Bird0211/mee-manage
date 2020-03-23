package com.mee.manage.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.BizMenu;
import com.mee.manage.vo.EditBizMenuVo;

/**
 * IBizMenuService
 */
public interface IBizMenuService extends IService<BizMenu> {

    List<BizMenu> getBizMenuByBizId(Long bizId);

    BizMenu addBizMenu(BizMenu bizMenu);
    
    boolean removeBizMenu(Long id);

    boolean updateBizMenu(EditBizMenuVo bizMenuVo);

}